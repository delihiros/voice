(defproject voice "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.twitter4j/twitter4j-core "4.0.2"]
                 [org.twitter4j/twitter4j-stream "4.0.2"]
                 [org.twitter4j/twitter4j-async "4.0.2"]
                 [clj-http "1.0.1"]
                 [clj-audio "0.1.0"]]
  :aot [voice.core]
  :main voice.core)
