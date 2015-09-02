(ns ^:figwheel-always om-kordano.core
    (:require  [cljs.reader :as reader]
               [goog.events :as events]
               [om.core :as om]
               [om.dom :as dom]
               [secretary.core :as sec :include-macros true]
               [goog.history.EventType :as HEventType])
    (:import goog.History
             [goog.net XhrIo]
             goog.net.EventType
             [goog.events EventType]))

;; --- HELPERS ---
(enable-console-print!)

(println "Greetings Lord Kordano!")

(sec/set-config! :prefix "#")

(let [history (History.)
      navigation HEventType/NAVIGATE]
  (goog.events/listen history
                     navigation
                     #(-> % .-token sec/dispatch!))
  (doto history (.setEnabled true)))

(def app-state
  (atom {:views []
         :welcome "Welcome!"}))

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

;; --- NAV ---
(defn nav-view 
  "Navigation bar"
  [data owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (edn-xhr
       {:method :get
        :url "data/views.edn"
        :on-complete #(om/transact! data :views (fn [_] %))}))
    om/IRender
    (render
      [this]
      (apply dom/nav nil
             (map
              (fn [{:keys [text url]}]
                (dom/a #js {:href url :className "nav-item"} (str text)))
              (:views data))))))

(om/root nav-view app-state {:target (. js/document (getElementById "nav-container"))})


;; --- ARTICLES ---
(defn articles-view
  "View that shows a short list of all published articles"
  [data owner]
  (reify om/IRender (render [_] (dom/div nil (dom/h2 nil "Articles")))))

(sec/defroute articles-page "/articles" []
  (om/root articles-view app-state {:target (. js/document (getElementById "main-container"))}))


;; --- LANDING PAGE ---
(defn index-view
  "Landing page"
  [data owner]
  (reify om/IRender (render [this] (dom/div nil (dom/h1 nil (:welcome data))))))

(sec/defroute index-page "/" []
  (om/root
   index-view
   app-state
   {:target (. js/document (getElementById "main-container"))}))

(-> js/document .-location (set! "#/"))

(defn on-js-reload [])
