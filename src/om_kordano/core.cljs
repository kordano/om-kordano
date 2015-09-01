(ns ^:figwheel-always om-kordano.core
    (:require [om.core :as om]
              [om.dom :as dom]
              [secretary.core :as sec :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))


;; --- HELPERS ---
(enable-console-print!)

(println "Greetings Lord Kordano!")

(sec/set-config! :prefix "#")

(let [history (History.)
      navigation EventType/NAVIGATE]
  (goog.events/listen history
                     navigation
                     #(-> % .-token sec/dispatch!))
  (doto history (.setEnabled true)))

(def app-state
  (atom {:views [{:text "home" :url "#/"}
                 {:text "articles" :url "#/articles"}
                 {:text "projects" :url "#/projects"}
                 {:text "services" :url "#/services"}
                 {:text "readings" :url "#/readings"}
                 {:text "about" :url "#/about"}]
         :welcome "Welcome!"}))

;; --- NAV ---
(defn nav-view 
  "Navigation bar"
  [data owner]
  (reify
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
