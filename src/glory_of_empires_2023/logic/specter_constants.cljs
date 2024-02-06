(ns glory-of-empires-2023.logic.specter-constants
  (:require [com.rpl.specter :as sp]))

;; Trick to allow cursive find these as constants in our main source code.
;; Since they are defined with specter macros, cursive does not recognize them as constants automatically.
(def MAP-VALS sp/MAP-VALS)
(def ALL sp/ALL)
(def FIRST sp/FIRST)
(def LAST sp/LAST)

;; Specter navigator that proceeds to map values like sp/MAP-VALS but collects for each value
;; also the key, so it can be used for final query or transform.
(sp/declarepath MAP-VALS-WITH-KEYS-RAW)
(sp/providepath MAP-VALS-WITH-KEYS-RAW [sp/ALL (sp/collect-one sp/FIRST) sp/LAST])

(def MAP-VALS-COLLECT-KEYS MAP-VALS-WITH-KEYS-RAW)