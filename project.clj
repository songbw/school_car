(defproject school-car "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [compojure "1.3.3"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [imintel/ring-xml "0.0.2"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [lobos "1.0.0-beta3"]
                 [postgresql "9.3-1102.jdbc41"]
                 [cheshire "5.4.0"]
                 [clj-json "0.5.3"]
                 [korma "0.4.0"]
                 [clj.qrgen "0.3.0"]
                 [clj-http "1.1.2"]
                 [com.taoensso/carmine "2.11.1"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [log4j/log4j "1.2.16"
                  :exclusions [javax.mail/mail
                               javax.jms/jms
                               com.sun.jdmk/jmxtools
                               com.sun.jmx/jmxri]]
                 ]
  :plugins [[lein-ring "0.9.3"]]
  :ring {:handler server.server/app}
  :main server.server
  :java-source-paths ["src/java"]
  :javac-options     ["-target" "1.7" "-source" "1.7"]
  :profiles {:dev {dependencies [[ring/ring-mock "0.2.0"]]}})
