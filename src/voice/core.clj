(ns voice.core
  (:require [clj-http.client :as client])
  (:import [twitter4j TwitterFactory TwitterStreamFactory StatusListener StatusAdapter UserStreamListener]
           [twitter4j.conf ConfigurationBuilder]
           [javafx.application Application]
           [javafx.scene.media Media MediaPlayer])
  (:gen-class
    :name VoiceTweet
    :main true
    :extends javafx.application.Application))

(def ^:dynamic *app-consumer-key* "")
(def ^:dynamic *app-consumer-secret* "")
(def ^:dynamic *user-access-token* "")
(def ^:dynamic *user-access-token-secret* "")

(def ^:dynamic *api-key* "") ; get API key from https://dev.smt.docomo.ne.jp/?p=index
(def ^:dynamic *docomo-url* (str "https://api.apigw.smt.docomo.ne.jp/voiceText/v1/textToSpeech" "?APIKEY=" *api-key*))

(defn voice
  [text]
  (client/post *docomo-url*
               {:form-params
                {:text text
                 :speaker "haruka"}
                :as :byte-array}))

(def configuration
  (let [config (doto (ConfigurationBuilder.)
                 (.setOAuthConsumerKey *app-consumer-key*)
                 (.setOAuthConsumerSecret *app-consumer-secret*)
                 (.setOAuthAccessToken *user-access-token*)
                 (.setOAuthAccessTokenSecret *user-access-token-secret*))]
    (.build config)))

(defn play
  [file]
  (-> file
      clojure.java.io/file .toURI str
      Media. MediaPlayer. .play))

(def listener
  (reify UserStreamListener
    (onDeletionNotice [this status-deletion-notice])
    (onScrubGeo [this user-id up-to-status-id])
    (onStallWarning [this warning])
    (onStatus [this status]
      (let [text (.getText status)
            voice-data (-> text voice :body)
            wav-file (str (.getId status) ".wav")]
        (println text)
        (.start (Thread. (fn []
                   (with-open [out (clojure.java.io/output-stream wav-file)]
                     (.write out voice-data)
                     (play wav-file)
                     (clojure.java.io/delete-file wav-file)))))))
    (onTrackLimitationNotice [this number-of-limited-statuses])
    (onBlock [this source blockedUser])
    (onDeletionNotice [this directMessageId userId])
    (onDirectMessage [this directMessage])
    (onFavorite [this source target favoritedStatus])
    (onFollow [this source followedUser])
    (onFriendList [this friendIds])
    (onUnblock [this source unblockedUser])
    (onUnfavorite [this source target unfavoritedStatus])
    (onUnfollow [this source unfollowedUser])
    (onUserListCreation [this listOwner list])
    (onUserListDeletion [this listOwner list])
    (onUserListMemberAddition [this addedMember listOwner list])
    (onUserListMemberDeletion [this deletedMember listOwner list])
    (onUserListSubscription [this subscriber listOwner list])
    (onUserListUnsubscription [this subscriber listOwner list])
    (onUserListUpdate [this listOwner list])
    (onUserProfileUpdate [this updatedUser])
    (onException [this e])))

(defn make-twitterStream []
  (.getInstance (TwitterStreamFactory. configuration)))

(defn -start
  [this stage]
  (let [twitter-stream (make-twitterStream)]
    (doto twitter-stream
      (.addListener listener)
      (.user))))

(defn -main [& args]
  (Application/launch (Class/forName "VoiceTweet") (into-array String [])))
