(ns chat.server.handler
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [api]]
            [compojure.core :refer [GET routes defroutes context]]
            [chat.server.sync :refer [sync-routes]]))

(defroutes site-routes
  (GET "/" []
    (-> "chat.html"
        (clojure.java.io/resource)
        (clojure.java.io/file)
        (slurp))))

(defroutes resource-routes
  (resources "/"))

(def app
  (-> (routes
        resource-routes
        site-routes
        sync-routes)
      api))

(defonce server (atom nil))

(defn stop-server!
  []
  (when-let [stop-fn @server]
    (stop-fn :timeout 100)))

(defn start-server!
  [port]
  (stop-server!)
  (reset! server (run-server #'app {:port port})))

(defn -main  [& args]
  (let [port (Integer/parseInt (first args))]
    (start-server! port)
    (println "starting on port " port)))


