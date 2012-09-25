(ns yetibot.commands.collections
  (:require
    [clojure.string :as s])
  (:use [yetibot.util :only (cmd-hook)]))

(defn ensure-items-collection [items]
  (if (coll? items)
    items
    (s/split items #"\n")))

; random
(defn random
  "random <list> # returns a random item where <list> is a comma-separated list of items.
  Can also be used to extract a random item when a collection is piped to random."
  [items]
  (rand-nth (ensure-items-collection items)))

(cmd-hook #"random"
          _ (random opts))

(def head-tail-regex #"(\d+).+")

; head
(defn head
  "head <list> # returns the first item from the <list>
head <n> <list> # return the first <n> items from the <list>"
  [n items]
  (let [f (if (= 1 n) first (partial take n))]
    (f (ensure-items-collection items))))

(cmd-hook #"head"
          #"(\d+)" (head (read-string (second p)) opts)
          _ (head 1 opts))

; tail
(defn tail
  "tail <list> # returns the last item from the <list>
tail <n> <list> # returns the last <n> items from the <list>"
  [n items]
  (let [f (if (= 1 n) last (partial take-last n))]
    (f (ensure-items-collection items))))

(cmd-hook #"tail"
          #"(\d+)" (tail (read-string (second p)) opts)
          _ (tail 1 opts))

; xargs
; example usage: !users | xargs attack
(defn xargs
  "xargs <cmd> <list> # run <cmd> for every item in <list>; behavior is similar to xargs(1)'s xargs -n1"
  [cmd items user]
  (if (s/blank? cmd)
    items
    (let [is (ensure-items-collection items)]
      (map #(yetibot.core/parse-and-handle-command (str cmd " " %) user nil) is))))

(cmd-hook #"xargs"
          _ (xargs args opts user))

;join
(defn join
  "join <list> # joins list with a single space"
  [items]
  (s/join " " (ensure-items-collection items)))

(cmd-hook #"join"
          _ (join opts))