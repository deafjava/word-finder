(ns bras-cubas.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure.java.jdbc :as sql]
            [taoensso.faraday :as far]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))



(def db {:dbtype   "mysql"
         :dbname   "literature"
         :host     "localhost"
         :port     "3307"
         :user     "root"
         :password "d4t4b4s3"})

(def dynamodb-client-opts {
                           :access-key "key"
                           :secret-key "key2"
                           :endpoint   "http://localhost:8000"
                           })

(defn get-book [id]
  (sql/query db [(str "select content from book where id = " id)] {:row-fn :content}))


(defn get-book-ids []
  (sql/query db ["select id, title from book"]))

(defn list-books
  [request]
  (ring-resp/response (get-book-ids)))

(defn find-word [word source]
  (->> source
       (re-seq (re-pattern word))
       count))

(defn research-dynamodb-first [id word]
  (far/get-item dynamodb-client-opts :researched-words-literature {:id id, :word word}))

(defn count-words-in-book
  [request]
  (let [word (get-in request [:query-params :w]) id (get-in request [:query-params :id])]
    (ring-resp/response {:total (get (research-dynamodb-first id word) (find-word word (nth (get-book id) 0)))})))

(defn home-page
  [request]
  (ring-resp/response "<html><body><h1>Word Researcher of Literature</h1></body></html>"))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

(def common-interceptors-json [(body-params/body-params) http/json-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/list" :get (conj common-interceptors-json `list-books)]
              ["/count" :get (conj common-interceptors-json `count-words-in-book)]})

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by bras-cubas.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env                     :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes            routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path     "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type              :jetty
              ;;::http/host "localhost"
              ::http/port              8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2?  false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})
