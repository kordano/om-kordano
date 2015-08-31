(ns ^:figwheel-always om-kordano.core
    (:require [cljs.reader :as reader]
              [goog.events :as events]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true])
    (:import [goog.net XhrIo]
             goog.net.EventType
             [goog.events EventType]))

(enable-console-print!)

(println "Greetings Lord Kordano!")

;; define your app data so that it doesn't get over-written on reload

(def app-state (atom {:text "Hello world!"
                          :sites [{:text "Home" :url "/"}
                                  {:text "Archives" :url "/archives"}
                                  {:text "Projects" :url "/projects"}
                                  {:text "About" :url "/about"}]}))
(def ^:private meths
  {:get "GET"
   :put "PUT"
   :post "POST"
   :delete "DELETE"})

(defn edn-xhr [{:keys [method url data on-complete]}]
  (let [xhr (XhrIo.)]
    (events/listen xhr goog.net.EventType.COMPLETE
      (fn [e]
        (on-complete (reader/read-string (.getResponseText xhr)))))
    (. xhr
      (send url (meths method) (when data (pr-str data))
        #js {"Content-Type" "application/edn"}))))

(defn nav-item [{:keys [text url]} owner]
  (reify
    om/IRender
    (render [this]
      (dom/a #js {:href url} text))))


(defn navbar [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (edn-xhr
       {:method :get
        :url "data/views"
        :on-complete #(om/transact! data :views (fn [_] %))}))
    om/IRender
    (render [this]
      (apply dom/nav nil (om/build-all nav-item (:views data))))))


(om/root
  navbar
  app-state
  {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

