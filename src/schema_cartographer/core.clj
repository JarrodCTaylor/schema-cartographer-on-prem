(ns schema-cartographer.core
  (:require
    [clojure.pprint :as pp]
    [datomic.api :as peer]
    [schema-cartographer.queries :as queries]
    [schema-cartographer.annotation-audit :refer [log-schema-audit]]
    [schema-cartographer.explorer :refer [explore]]
    [schema-cartographer.schema :refer [data-map]]))

 (defn save-schema-edn [db schema-file-name]
   (let [raw-schema (queries/annotated-schema db)
         schema-data (data-map raw-schema)
         output-location schema-file-name]
     (spit output-location (with-out-str (pp/pprint schema-data)))
     (println (str "== " output-location " successfully saved. =="))))

(defn save-explore-schema-edn [db schema-file-name ref-search-limit]
  (let [_ (println "== Querying Schema ==\n")
        raw-schema (queries/unannotated-schema {:db db})
        _ (println "-- Exploring Database --\n")
        schema-data (explore db raw-schema ref-search-limit)
        output-location schema-file-name]
    (spit output-location (with-out-str (pp/pprint schema-data)))
    (println (str "== " output-location " successfully saved. =="))))

(comment
  (def db-uri "datomic:<<DATOMIC-URI>>")
  (def conn (peer/connect db-uri))
  (def db (peer/db conn))
  (save-explore-schema-edn db "resources/<<YOUR-DB-SCHEMA>>.edn" nil))