(ns schema-cartographer.queries
  (:require
    [datomic.api :as peer]))

(defn annotated-schema [db]
  (let [query '[:find (pull ?e [:db/ident
                                {:db/valueType [:db/ident]}
                                {:db/cardinality [:db/ident]}
                                :db.attr/preds
                                {:db/unique [:db/ident]}
                                :db/isComponent
                                :db/noHistory
                                :db/tupleAttrs
                                :db.entity/attrs
                                :db.entity/preds
                                :cartographer/enumeration
                                :cartographer/entity
                                {:cartographer/replaced-by [:db/ident]}
                                {:cartographer/references-namespaces [:cartographer/entity :cartographer/enumeration]}
                                {:cartographer/validates-namespace [:cartographer/entity]}
                                :cartographer/deprecated?
                                :db/doc])
                :where (or [?e :db/ident]
                           [?e :cartographer/entity]
                           [?e :cartographer/enumeration])]
        everything (map first (peer/q query db))
        schema-attrs (->> everything
                          (filter :db/ident)
                          (filter (fn [{:db/keys [ident]}]
                                    (re-matches #"^(?!cartographer)(?!db)(?!fressian).+" (namespace ident)))))
        meta-schema-schema (filter #(-> % :db/ident not) everything)]
    (into meta-schema-schema schema-attrs)))

(defn unannotated-schema
  [{:keys [db]}]
  (let [query '[:find (pull ?element [:db/ident
                                      {:db/valueType [:db/ident]}
                                      {:db/cardinality [:db/ident]}
                                      :db.attr/preds
                                      {:db/unique [:db/ident]}
                                      :db/isComponent
                                      :db/noHistory
                                      :db/tupleAttrs
                                      :db.entity/attrs
                                      :db.entity/preds
                                      :db/doc])
                :where [?e :db/ident ?element]]
        everything (peer/q query db)
        ident-attrs (->> everything
                         (map first)
                         (filter :db/ident))]
    (filter (fn [{:db/keys [ident]}]
              (re-matches #"^(?!db)(?!fressian).+" (or (namespace ident) ""))) ident-attrs)))