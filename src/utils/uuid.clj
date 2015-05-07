(ns utils.uuid
  (:use [utils.common
         :only [pid device-mac-bytes
                now-seconds mac-bytes-to-string
                string-bytes bytes-short
                decimal-biginteger]])
  (:import (java.nio ByteBuffer ByteOrder))
  (:import (utils Common Common)))

(let [counter (atom 0)]
  (defn counter-step []
    (swap! counter inc))
  (defn counter-zero []
    (reset! counter 0)))

(let [last-time (atom 0)]
  (defn get-last-time []
    @last-time)
  (defn set-last-time [timestamp]
    (reset! last-time timestamp)))

(defn- make-counter
  [last-time timestamp]
  (if (== last-time timestamp)
    (counter-step)
    (counter-zero)))

(defn- make-uuid
  "uuid = TIMESTAMP(4bytes) + MAC(6bytes) + PID(2bytes) + COUNTER(4byte)
   Return BigDecimal as uuid"
  [timestamp mac-bytes pid-short counter]
  (let [buff (ByteBuffer/allocate 16)]
    (.putInt buff timestamp)
    (.put buff mac-bytes)
    (.putShort buff pid-short)
    (.putInt buff counter)
    (let [big-int (new BigInteger (.array buff))]
      (bigdec big-int))))

(defn gen-uuid
  []
  (let [timestamp (now-seconds)
       ;;; mac-bytes (device-mac-bytes)
        mac-bytes (Common/getMachineMacByIP)
        pid-bytes (string-bytes (pid))
        unsigned-short (bytes-short pid-bytes)
        last-time (get-last-time)
        counter (make-counter last-time timestamp)]
    (set-last-time timestamp)
    (make-uuid timestamp mac-bytes unsigned-short counter)))

(defn ungen-uuid
  [big-dec]
  (let [mac-bytes (make-array Byte/TYPE 6)]
    (let [to-bytes-array (.toByteArray (decimal-biginteger big-dec))
          buff (ByteBuffer/wrap to-bytes-array)]
      (let [timestamp (.getInt buff)]
        (.get buff mac-bytes 0 6)
        (let [mac-string (mac-bytes-to-string mac-bytes)
              pid-short (.getShort buff)
              counter (.getInt buff)]
          (list timestamp mac-string pid-short counter))))))
