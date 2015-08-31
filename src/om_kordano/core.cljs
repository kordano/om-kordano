(ns ^:figwheel-always om-kordano.core
    (:require [om.core :as om :include-macros true]
              [om-tools.dom :as dom :include-macros true]
              [om-tools.core :refer-macros [defcomponent]]))


;; --- HELPERS ---
(enable-console-print!)

(println "Greetings Lord Kordano!")

;; --- APP ---
(def app-state
  (atom {:views [{:text :home :url "/"}
                 {:text :articles :url "/articles"} {:text :projects :url "/projects"}
                 {:text :services :url "/services"} {:text :readings :url "/readings"}
                 {:text :about :url "/about"}]}))

(defcomponent nav-item [{:keys [text url]} owner]
  (render [this]
          (dom/a {:href url} (str text))))

(defcomponent nav-view [data owner]
  (render
   [this]
   (dom/nav nil (om/build-all nav-item (:views data)))))

(om/root
 nav-view
 app-state {:target (. js/document (getElementById "app"))})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
