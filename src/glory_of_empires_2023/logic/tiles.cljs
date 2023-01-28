(ns glory-of-empires-2023.logic.tiles
  (:require [medley.core :refer [index-by]]))

(def tile-width 432)
(def tile-height 376)
(def tile-size [tile-width tile-height])

(defn screen-loc [[logical-x logical-y]]
  {:pre [(integer? logical-x) (integer? logical-y)]} ;; use malli instrumentation
  [(* logical-x tile-width 0.75)
   (* tile-height (+ (* logical-x 0.5) logical-y))])

(def planet-systems-arr
  [; 1 planet
   {:id :aah, :type :1planet, :image "1planet/Tile-Aah.gif" :planets {:aah {:res 1 :inf 1 :loc [0 0]}}}
   {:id :acheron, :type :1planet, :image "1planet/Tile-Acheron.gif" :planets {:acheron {:res 1 :inf 2 :tech {:green 1} :loc [0 0]}}}
   {:id :aeon, :type :1planet, :image "1planet/Tile-Aeon.gif" :planets {:aeon {:res 1 :inf 3 :tech {:green 1} :loc [0 0]}}}
   {:id :aker, :type :1planet, :image "1planet/Tile-Aker.gif" :planets {:aker {:res 1 :inf 2 :loc [0 0]}}}
   {:id :ammit, :type :1planet, :image "1planet/Tile-Ammit.gif" :planets {:ammit {:res 1 :inf 0 :loc [0 0]}}}
   {:id :amun, :type :1planet, :image "1planet/Tile-Amun.gif" :planets {:amun {:res 0 :inf 1 :loc [0 0]}}}
   {:id :andjety, :type :1planet, :image "1planet/Tile-Andjety.gif" :planets {:andjety {:res 3 :inf 0 :loc [0 0]}}}
   {:id :anhur, :type :1planet, :image "1planet/Tile-Anhur.gif" :planets {:anhur {:res 2 :inf 1 :loc [0 0]}}}
   {:id :ankh, :type :1planet, :image "1planet/Tile-Ankh.gif" :planets {:ankh {:res 2 :inf 2 :loc [0 0]}}}
   {:id :anuket, :type :1planet, :image "1planet/Tile-Anuket.gif" :planets {:anuket {:res 1 :inf 1 :loc [0 0]}}}
   {:id :apis, :type :1planet, :image "1planet/Tile-Apis.gif" :planets {:apis {:res 0 :inf 2 :loc [0 0]}}}
   {:id :asgard3, :type :1planet, :image "1planet/Tile-Asgard_III.gif"
    :planets {:asgard3 {:res 1 :inf 3 :tech {:red 1} :loc [0 0]
                        :special "Occupier gains: Built-in space-dock unit,
                        3 PDS with deep-space-cannon, 9 fighter support.
                        Invading ground forces -1 on combat."}}}
   {:id :astennu, :type :1planet, :image "1planet/Tile-Astennu.gif" :planets {:astennu {:res 2 :inf 0 :loc [0 0]}}}
   {:id :aten, :type :1planet, :image "1planet/Tile-Aten.gif" :planets {:aten {:res 0 :inf 3 :loc [0 0]}}}
   {:id :babi, :type :1planet, :image "1planet/Tile-Babi.gif" :planets {:babi {:res 2 :inf 0 :loc [0 0]}}}
   {:id :bakha, :type :1planet, :image "1planet/Tile-Bakha.gif" :planets {:bakha {:res 2 :inf 3 :loc [0 0]}}}
   {:id :bast, :type :1planet, :image "1planet/Tile-Bast.gif" :planets {:bast {:res 1 :inf 1 :loc [0 0]}}}
   {:id :beriyil, :type :1planet, :image "1planet/Tile-Beriyil.gif" :planets {:beriyil {:res 1 :inf 1 :loc [0 0]}}}
   {:id :bes, :type :1planet, :image "1planet/Tile-Bes.gif" :planets {:bes {:res 1 :inf 1 :loc [0 0]}}}
   {:id :capha, :type :1planet, :image "1planet/Tile-Capha.gif" :planets {:capha {:res 3 :inf 0 :loc [0 0]}}}
   {:id :chensit, :type :1planet, :image "1planet/Tile-Chensit.gif" :planets {:chensit {:res 1 :inf 1 :loc [0 0]}}}
   {:id :chnum, :type :1planet, :image "1planet/Tile-Chnum.gif" :planets {:chnum {:res 2 :inf 0 :loc [0 0]}}}
   {:id :chuuka, :type :1planet, :image "1planet/Tile-Chuuka.gif" :planets {:chuuka {:res 1 :inf 0 :tech {:blue 1} :loc [0 0]}}}
   {:id :cicerus, :type :1planet, :image "1planet/Tile-Cicerus.gif" :planets {:cicerus {:res 2 :inf 2 :loc [0 0]}}}
   {:id :coruscant, :type :1planet, :image "1planet/Tile-Coruscant.gif" :planets {:coruscant {:res 2 :inf 4 :loc [0 0]}}}
   {:id :dedun, :type :1planet, :image "1planet/Tile-Dedun.gif" :planets {:dedun {:res 2 :inf 0 :tech {:green 1} :loc [0 0]}}}
   {:id :deimo, :type :1planet, :image "1planet/Tile-Deimo.gif" :planets {:deimo {:res 0 :inf 1 :loc [0 0]
                                                                                  :special "Occupier gains: Built-in space-dock with no fighter support and unlimited prod capacity."}}}
   {:id :discworld, :type :1planet, :image "1planet/Tile-Discworld.gif" :planets {:discworld {:res 3 :inf 3 :tech {:any 1} :loc [0 0]}}}
   {:id :elnath, :type :1planet, :image "1planet/Tile-Elnath.gif" :planets {:elnath {:res 2 :inf 0 :tech {:blue 1} :loc [0 0]}}}
   {:id :everra, :type :1planet, :image "1planet/Tile-Everra.gif" :planets {:everra {:res 3 :inf 1 :loc [0 0]}}}
   {:id :faunus, :type :1planet, :image "1planet/Tile-Faunus.gif" :planets {:faunus {:res 1 :inf 3 :tech {:green 2} :loc [0 0]}}}
   {:id :fiorina, :type :1planet, :image "1planet/Tile-Fiorina.gif" :planets {:fiorina {:res 2 :inf 1 :tech {:green 1} :loc [0 0]}}}
   {:id :floyd4, :type :1planet, :image "1planet/Tile-FloydIV.gif" :planets {:floyd4 {:res 0 :inf 2 :tech {:red 1} :loc [0 0]}}}
   {:id :garbozia, :type :1planet, :image "1planet/Tile-Garbozia.gif" :planets {:garbozia {:res 2 :inf 1 :tech {:green 1} :loc [0 0]}}}
   {:id :heimat, :type :1planet, :image "1planet/Tile-Heimat.gif"
    :planets {:heimat {:res 3 :inf 2 :loc [0 0]
                       :special "Fleet supply limit may be exceeded in this system by 1"}}}
   {:id :hopes-end, :type :1planet, :image "1planet/Tile-Hopes_End.gif"
    :planets {:hopes-end {:res 3 :inf 0 :loc [0 0]
                          :special "May be exhausted for 2 shock-troops"}}}
   {:id :inaak, :type :1planet, :image "1planet/Tile-Inaak.gif" :planets {:inaak {:res 0 :inf 2 :tech {:yellow 1} :loc [0 0]}}}
   {:id :industrex, :type :1planet, :image "1planet/Tile-Industrex.gif" :planets {:industrex {:res 2 :inf 0 :tech {:red 2} :loc [0 0]}}}
   {:id :iskra, :type :1planet, :image "1planet/Tile-Iskra.gif"
    :planets {:iskra {:res 2 :inf 3 :loc [0 0] :special "Can be exhausted for an action-card"}}}
   {:id :ithaki, :type :1planet, :image "1planet/Tile-Ithaki.gif" :planets {:ithaki {:res 3 :inf 2 :special "???" :loc [0 0]}}}
   {:id :kanite, :type :1planet, :image "1planet/Tile-Kanite.gif" :planets {:kanite {:res 1 :inf 1 :loc [-75 -70]}}}
   {:id :kauket, :type :1planet, :image "1planet/Tile-Kauket.gif" :planets {:kauket {:res 2 :inf 1 :loc [0 0]}}}
   {:id :kazenoeki, :type :1planet, :image "1planet/Tile-Kazenoeki.gif" :planets {:kazenoeki {:res 2 :inf 3 :special "???" :loc [0 0]}}}
   {:id :khepri, :type :1planet, :image "1planet/Tile-Khepri.gif" :planets {:khepri {:res 2 :inf 0 :loc [0 0]}}}
   {:id :khnum, :type :1planet, :image "1planet/Tile-Khnum.gif" :planets {:khnum {:res 1 :inf 1 :loc [0 0]}}}
   {:id :klendathu, :type :1planet, :image "1planet/Tile-Klendathu.gif"
    :planets {:klendathu {:res 5 :inf 0 :loc [0 0]
                          :special "Bug guarantine zone, PDS when non-occupied"}}}
   {:id :kobol, :type :1planet, :image "1planet/Tile-Kobol.gif" :planets {:kobol {:res 2 :inf 5 :loc [0 0]}}}
   {:id :laurin, :type :1planet, :image "1planet/Tile-Laurin.gif" :planets {:laurin {:res 2 :inf 1 :loc [0 0]}}}
   {:id :lesab, :type :1planet, :image "1planet/Tile-Lesab.gif" :planets {:lesab {:res 2 :inf 1 :tech {:green 1} :loc [0 0]}}}
   {:id :lodor, :type :1planet, :image "1planet/Tile-Lodor.gif" :planets {:lodor {:res 3 :inf 1 :tech {:green 1} :loc [0 0]}}}
   {:id :lv426, :type :1planet, :image "1planet/Tile-Lv426.gif" :planets {:lv426 {:res 1 :inf 0 :tech {:green 1 :red 1} :special "Hazard" :loc [0 0]}}}
   {:id :mehar-xull, :type :1planet, :image "1planet/Tile-Mehar_Xull.gif" :planets {:mehar-xull {:res 1 :inf 3 :tech {:blue 1} :loc [0 0]}}}
   {:id :mirage, :type :1planet, :image "1planet/Tile-Mirage.gif"
    :planets {:mirage {:res 1 :inf 2 :special "Can be exhausted for 2 fighters" :loc [0 0]}}}
   {:id :myrkr, :type :1planet, :image "1planet/Tile-Myrkr.gif" :planets {:myrkr {:res 1 :inf 2 :loc [0 0]}}}
   {:id :nanan, :type :1planet, :image "1planet/Tile-Nanan.gif" :planets {:nanan {:res 1 :inf 2 :tech {:blue 1} :loc [0 0]}}}
   {:id :natthar, :type :1planet, :image "1planet/Tile-Natthar.gif" :planets {:natthar {:res 3 :inf 0 :loc [0 0]}}}
   {:id :nef, :type :1planet, :image "1planet/Tile-Nef.gif" :planets {:nef {:res 2 :inf 0 :tech {:red 1} :loc [0 0]}}}
   {:id :nexus, :type :1planet, :image "1planet/Tile-Nexus.gif" :planets {:nexus {:res 0 :inf 3 :loc [0 0]}}}
   {:id :niiwa-se, :type :1planet, :image "1planet/Tile-Niiwa-Sei.gif" :planets {:niiwa-sei {:res 3 :inf 2 :tech {:yellow 1} :loc [0 0]}}}
   {:id :pakhet, :type :1planet, :image "1planet/Tile-Pakhet.gif" :planets {:pakhet {:res 1 :inf 1 :loc [0 0]}}}
   {:id :parzifal, :type :1planet, :image "1planet/Tile-Parzifal.gif" :planets {:parzifal {:res 4 :inf 3 :tech {:green 1} :loc [0 0]}}}
   {:id :perimeter, :type :1planet, :image "1planet/Tile-Perimeter.gif" :planets {:perimeter {:res 2 :inf 2 :loc [0 0]}}}
   {:id :petbe, :type :1planet, :image "1planet/Tile-Petbe.gif" :planets {:petbe {:res 1 :inf 1 :loc [0 0]}}}
   {:id :primor, :type :1planet, :image "1planet/Tile-Primor.gif" :planets {:primor {:res 2 :inf 1 :loc [0 0] :special "Exhaust for 2 GF"}}}
   {:id :ptah, :type :1planet, :image "1planet/Tile-Ptah.gif" :planets {:ptah {:res 1 :inf 1 :loc [0 0]}}}
   {:id :qetesh, :type :1planet, :image "1planet/Tile-Qetesh.gif" :planets {:qetesh {:res 3 :inf 1 :tech {:green 1} :loc [-75 -70]}}}
   {:id :quann, :type :1planet, :image "1planet/Tile-Quann.gif" :planets {:quann {:res 2 :inf 1 :tech {:green 1} :loc [-75 -70]}}}
   {:id :radon, :type :1planet, :image "1planet/Tile-Radon.gif" :planets {:radon {:res 1 :inf 3 :loc [75 -70] :special "???"}}}
   {:id :saudor, :type :1planet, :image "1planet/Tile-Saudor.gif" :planets {:saudor {:res 2 :inf 2 :loc [0 0]}}}
   {:id :sem-lore, :type :1planet, :image "1planet/Tile-Sem-Lore.gif" :planets {:sem-lore {:res 3 :inf 2 :tech {:yellow 1} :loc [0 0]}}}
   {:id :shai, :type :1planet, :image "1planet/Tile-Shai.gif" :planets {:shai {:res 3 :inf 0 :loc [0 0]}}}
   {:id :shool, :type :1planet, :image "1planet/Tile-Shool.gif" :planets {:shool {:res 2 :inf 2 :tech {:yellow 1} :loc [-75 -70]}}}
   {:id :solitude, :type :1planet, :image "1planet/Tile-Solitude.gif"
    :planets {:solitude {:res 3 :inf 4 :loc [0 0]
                         :special "Max 1 GF on planet at status (rest killed). No PDS, No Space-dock possible."}}}
   {:id :sulako, :type :1planet, :image "1planet/Tile-Sulako.gif" :planets {:sulako {:res 2 :inf 3 :loc [-75 -70]}}}
   {:id :suuth, :type :1planet, :image "1planet/Tile-Suuth.gif"
    :planets {:suuth {:res 2 :inf 2 :loc [0 0]
                      :special "First to conquer gains free tech"}}}
   {:id :tarmann, :type :1planet, :image "1planet/Tile-Tarmann.gif" :planets {:tarmann {:res 1 :inf 1 :loc [0 0]}}}
   {:id :tefnut, :type :1planet, :image "1planet/Tile-Tefnut.gif" :planets {:tefnut {:res 3 :inf 1 :tech {:green 1} :loc [-75 -70]}}}
   {:id :tempesta, :type :1planet, :image "1planet/Tile-Tempesta.gif" :planets {:tempesta {:res 1 :inf 1 :tech {:blue 2} :loc [0 0]}}}
   {:id :tenenit, :type :1planet, :image "1planet/Tile-Tenenit.gif" :planets {:tenenit {:res 1 :inf 1 :loc [0 0]}}}
   {:id :theom, :type :1planet, :image "1planet/Tile-Theom.gif" :planets {:theom {:res 3 :inf 4 :tech {:green 1} :loc [-70 50]}}}
   {:id :thibah, :type :1planet, :image "1planet/Tile-Thibah.gif" :planets {:thibah {:res 1 :inf 1 :loc [0 0]}}}
   {:id :ubuntu, :type :1planet, :image "1planet/Tile-Ubuntu.gif" :planets {:ubuntu {:res 2 :inf 1 :loc [0 0]}}}
   {:id :wadjet, :type :1planet, :image "1planet/Tile-Wadjet.gif" :planets {:wadjet {:res 3 :inf 1 :tech {:green 1} :loc [-75 -70]}}}
   {:id :vefut2, :type :1planet, :image "1planet/Tile-Vefut_II.gif" :planets {:vefut2 {:res 2 :inf 0 :tech {:red 1} :loc [0 0]}}}
   {:id :wellon, :type :1planet, :image "1planet/Tile-Wellon.gif" :planets {:wellon {:res 1 :inf 2 :loc [0 0]}}}
   {:id :wepwawet, :type :1planet, :image "1planet/Tile-Wepwawet.gif" :planets {:wepwawet {:res 2 :inf 1 :loc [0 0]}}}
   {:id :cormund, :type :1planet, :image "1planet/Tile-Cormund.gif" :planets {:cormund {:res 2 :inf 0 :special "Gravity rift" :loc [0 0]}}}

   ; 2 planets
   {:id :abyz-fria, :type :2planet, :image "2planet/Tile-Abyz-Fria.gif"
    :planets {:abyz {:res 3 :inf 0 :loc [-75 -70]}, :fria {:res 2 :inf 0 :tech {:blue 1} :loc [75 70]}}}
   {:id :achill, :type :2planet, :image "2planet/Tile-Achill.gif"
    :planets {:achill-ii {:res 2 :inf 2 :loc [-75 -70]}, :achill-iv {:res 2 :inf 2 :loc [75 70]}}}
   {:id :aeterna-tammuz, :type :2planet, :image "2planet/Tile-AeternaTammuz.gif"
    :planets {:aeterna {:res 1 :inf 3 :loc [-75 -70]}, :tammuz {:res 2 :inf 1 :loc [75 70]}}}
   {:id :arinam-meer, :type :2planet, :image "2planet/Tile-Arinam-Meer.gif"
    :planets {:arinam {:res 1 :inf 2 :tech {:blue 1} :loc [-75 -70]}, :meer {:res 0 :inf 4 :loc [75 70]}}}
   {:id :arnor-lor, :type :2planet, :image "2planet/Tile-Arnor-Lor.gif"
    :planets {:arnor {:res 2 :inf 1 :loc [-75 -70]}, :lor {:res 1 :inf 2 :tech {:red 1} :loc [75 70]}}}
   {:id :aztlan-senhora, :type :2planet, :image "2planet/Tile-Aztlan-Senhora.gif"
    :planets {:aztlan {:res 2 :inf 1 :loc [-75 -70] :special "???"},
              :senhora-da-sorte {:res 1 :inf 2 :special "???" :loc [75 70]}}}
   {:id :bereg-lirta-iv, :type :2planet, :image "2planet/Tile-Bereg-Lirta_IV.gif"
    :planets {:bereg {:res 3 :inf 1 :tech {:red 1} :loc [-75 -70]}
              :lirta-iv {:res 2 :inf 3 :tech {:green 1} :loc [75 70]}}}
   {:id :bondar-maclean, :type :2planet, :image "2planet/Tile-Bondar-MacLean.gif"
    :planets {:bondar {:res 1 :inf 3 :special "science station" :loc [-75 -70]}
              :maclean {:res 0 :inf 2 :tech {:any 1} :loc [75 70]}}}
   {:id :cato, :type :2planet, :image "2planet/Tile-Cato.gif"
    :planets {:cato-i {:res 2 :inf 1 :tech {:blue 1} :loc [-75 -70]}
              :cato-ii {:res 1 :inf 1 :loc [75 70]}}}
   {:id :centauri-gral, :type :2planet, :image "2planet/Tile-Centauri-Gral.gif"
    :planets {:centauri {:res 1 :inf 3 :loc [-75 -70]}
              :gral {:res 1 :inf 1 :tech {:blue 1} :loc [75 70]}}}
   {:id :coorneeq-resculon, :type :2planet, :image "2planet/Tile-Coorneeq-Resculon.gif"
    :planets {:coorneeq {:res 1 :inf 2 :tech {:red 1} :loc [-75 -70]}
              :resculon {:res 2 :inf 0 :loc [75 70]}}}
   {:id :custos-xhal, :type :2planet, :image "2planet/Tile-Custos-Xhal.gif"
    :planets {:custos {:res 2 :inf 2 :loc [-75 -70] :special "Exhaust for 2 TG"}
              :xhal {:res 3 :inf 0 :loc [75 70]}}}
   {:id :dal-bootha-xxehan, :type :2planet, :image "2planet/Tile-Dal_Bootha-Xxehan.gif"
    :planets {:dal-bootha {:res 0 :inf 2 :tech {:red 1} :loc [-75 -70]}
              :xxehan {:res 1 :inf 1 :tech {:green 1} :loc [75 70]}}}
   {:id :garneau-hadfield, :type :2planet, :image "2planet/Tile-Garneau-Hadfield.gif"
    :planets {:garneau {:res 1 :inf 3 :special "science station" :loc [-75 -70]}
              :hadfield {:res 0 :inf 2 :tech {:any 1} :loc [75 70]}}}
   {:id :gilea-xerel-wu, :type :2planet, :image "2planet/Tile-Gilea-Xerel_Wu.gif"
    :planets {:gilea {:res 1 :inf 1 :loc [-75 -70]}
              :xerel-wu {:res 2 :inf 2 :tech {:yellow 1} :loc [75 70]}}}
   {:id :gurmee-uguza, :type :2planet, :image "2planet/Tile-Gurmee-Uguza.gif"
    :planets {:gurmee {:res 1 :inf 1 :loc [-75 -70]}
              :uguza {:res 0 :inf 3 :tech {:red 1} :loc [75 70]}}}
   {:id :hana, :type :2planet, :image "2planet/Tile-Hana.gif"
    :planets {:hana-major {:res 3 :inf 1 :loc [-75 -70]}
              :hana-minor {:res 0 :inf 1 :tech {:yellow 1} :loc [75 70]}}}
   {:id :hercalor-tiamat, :type :2planet, :image "2planet/Tile-Hercalor-Tiamat.gif"
    :planets {:hercalor {:res 1 :inf 0 :tech {:yellow 1} :loc [-75 -70]}
              :tiamat {:res 1 :inf 2 :tech {:yellow 1} :loc [75 70]}}}
   {:id :kazaros-mirabar, :type :2planet, :image "2planet/Tile-Kazaros-Mirabar.gif"
    :planets {:kazaros {:res 0 :inf 0 :loc [-75 -70]}
              :mirabar {:res 2 :inf 3 :loc [75 70]}}}
   {:id :lazar-sakulag, :type :2planet, :image "2planet/Tile-Lazar-Sakulag.gif"
    :planets {:lazar {:res 1 :inf 0 :loc [-75 -70]}
              :sakulag {:res 2 :inf 1 :loc [75 70]}}}
   {:id :lisis-velnor, :type :2planet, :image "2planet/Tile-Lisis-Velnor.gif"
    :planets {:lisis {:res 2 :inf 2 :loc [-75 -70]}
              :velnor {:res 2 :inf 0 :tech {:red 1} :loc [75 70]}}}
   {:id :machall-sorkragh, :type :2planet, :image "2planet/Tile-MachallSorkragh.gif"
    :planets {:machall {:res 1 :inf 2 :loc [-75 -70]}
              :sorkragh {:res 1 :inf 2 :loc [75 70]}}}
   {:id :masada-jamiah, :type :2planet, :image "2planet/Tile-Masada-Jamiah.gif"
    :planets {:masada {:res 0 :inf 0 :special "Fleet supply +2 in this system for the controller" :loc [-75 -70]}
              :jamiah {:res 4 :inf 0 :loc [75 70]}}}
   {:id :massada-iuddaea, :type :2planet, :image "2planet/Tile-Massada-Iuddaea.gif"
    :planets {:massada {:res 3 :inf 1 :special "Fleet supply +1 in this system for the controller" :loc [-75 -70]}
              :iuddaea {:res 1 :inf 1 :loc [75 70]}}}
   {:id :mellon-zohbat, :type :2planet, :image "2planet/Tile-Mellon-Zohbat.gif"
    :planets {:mellon {:res 0 :inf 2 :loc [-75 -70]}
              :zohbat {:res 3 :inf 1 :tech {:blue 1} :loc [75 70]}}}
   {:id :nadir-lucus, :type :2planet, :image "2planet/Tile-Nadir-Lucus.gif"
    :planets {:nadir {:res 2 :inf 0 :loc [-75 -70]}
              :lucus {:res 0 :inf 3 :tech {:green 1} :loc [75 70]}}}
   {:id :new-albion-starpoint, :type :2planet, :image "2planet/Tile-New_Albion-Starpoint.gif"
    :planets {:new-albion {:res 1 :inf 1 :tech {:green 1} :loc [-75 -70]}
              :starpoint {:res 3 :inf 1 :loc [75 70]}}}
   {:id :nummantia-hisspania, :type :2planet, :image "2planet/Tile-Nummantia-Hisspania.gif"
    :planets {:nummantia {:res 3 :inf 1 :special "Fleet supply +1 in this system for the controller" :loc [-75 -70]}
              :hisspania {:res 1 :inf 1 :loc [75 70]}}}
   {:id :othor-aiel, :type :2planet, :image "2planet/Tile-OthorAiel.gif"
    :planets {:othor {:res 1 :inf 3 :tech {:green 1} :loc [-75 -70]}
              :aiel {:res 2 :inf 0 :tech {:blue 1} :loc [75 70]}}}
   {:id :pankow-prenzlberg, :type :2planet, :image "2planet/Tile-Pankow-Prenzlberg.gif"
    :planets {:pankow {:res 3 :inf 1 :loc [-75 -70]}
              :prenzlberg {:res 1 :inf 3 :loc [75 70]}}}
   {:id :perro-senno, :type :2planet, :image "2planet/Tile-Perro-Senno.gif"
    :planets {:perro {:res 2 :inf 2 :loc [-75 -70]}
              :senno {:res 3 :inf 1 :tech {:blue 1} :loc [75 70]}}}
   {:id :qucenn-rarron, :type :2planet, :image "2planet/Tile-Qucenn-Rarron.gif"
    :planets {:qucenn {:res 1 :inf 2 :loc [-75 -70]}
              :rarron {:res 0 :inf 3 :tech {:green 1} :loc [75 70]}}}
   {:id :renenutet-osiris, :type :2planet, :image "2planet/Tile-Renenutet-Osiris.gif"
    :planets {:renenutet {:res 1 :inf 1 :tech {:green 1} :loc [-75 -70]}
              :osiris {:res 1 :inf 1 :loc [75 70]}}}
   {:id :renpet-tawaret, :type :2planet, :image "2planet/Tile-Renpet-Tawaret.gif"
    :planets {:renpet {:res 0 :inf 2 :loc [-75 -70]}
              :tawaret {:res 1 :inf 0 :loc [75 70]}}}
   {:id :resheph-ogdoad, :type :2planet, :image "2planet/Tile-Resheph-Ogdoad.gif"
    :planets {:resheph {:res 0 :inf 2 :tech {:red 1} :loc [-75 -70]}
              :ogdoad {:res 1 :inf 0 :loc [75 70]}}}
   {:id :salem-iogu, :type :2planet, :image "2planet/Tile-SalemIogu.gif"
    :planets {:salem {:res 0 :inf 1 :tech {:blue 1} :loc [-75 -70]}
              :iogu {:res 1 :inf 0 :tech {:green 1} :loc [75 70]}}}
   {:id :shiva-wishnu, :type :2planet, :image "2planet/Tile-Shiva-Wishnu.gif"
    :planets {:shiva {:res 0 :inf 0 :tech {:red 1} :loc [75 -70]}
              :wishnu {:res 2 :inf 2 :special "???" :loc [-75 70]}}}
   {:id :sulla-martinez, :type :2planet, :image "2planet/Tile-SullaMartinez.gif"
    :planets {:sulla {:res 2 :inf 0 :tech {:blue 1} :loc [-75 -70]}
              :martinez {:res 2 :inf 2 :tech {:red 1} :loc [75 70]}}}
   {:id :sumerian-arcturus, :type :2planet, :image "2planet/Tile-Sumerian-Arcturus.gif"
    :planets {:sumerian {:res 2 :inf 2 :special "Can be exhausted for 2 TG" :loc [-75 -70]}
              :arcturus {:res 1 :inf 1 :loc [75 70]}}}
   {:id :tequran-torkan, :type :2planet, :image "2planet/Tile-Tequran-Torkan.gif"
    :planets {:tequran {:res 2 :inf 0 :tech {:red 1} :loc [-75 -70]}
              :torkan {:res 0 :inf 3 :tech {:blue 1} :loc [75 70]}}}
   {:id :thoth-mafdet, :type :2planet, :image "2planet/Tile-Thoth-Mafdet.gif"
    :planets {:thoth {:res 1 :inf 1 :loc [-75 -70]}
              :mafdet {:res 1 :inf 1 :loc [75 70]}}}
   {:id :tsion-bellatrix, :type :2planet, :image "2planet/Tile-Tsion-Bellatrix.gif"
    :planets {:tsion {:res 2 :inf 2 :special "Can be exhausted for 2 TG" :loc [-75 -70]}
              :bellatrix {:res 0 :inf 1 :tech {:red 1} :loc [75 70]}}}
   {:id :vega, :type :2planet, :image "2planet/Tile-Vega.gif"
    :planets {:vega-minor {:res 1 :inf 2 :tech {:blue 1} :loc [-75 -70]}
              :vega-major {:res 2 :inf 1 :loc [75 70]}}}
   {:id :verne-quebec, :type :2planet, :image "2planet/Tile-Verne-Quebec.gif"
    :planets {:verne {:res 0 :inf 3 :special "Exhaust for action card" :loc [-75 -70]}
              :nouveau-quebec {:res 1 :inf 2 :special "Exhaust for action card" :loc [75 70]}}}
   {:id :vortekai-deguzz, :type :2planet, :image "2planet/Tile-Vortekai-Deguzz.gif"
    :planets {:vortekai {:res 4 :inf 1 :loc [-75 -70]}
              :deguzz {:res 1 :inf 2 :loc [75 70]}}}

   ; 3 planets
   {:id :ashtroth-loki-abaddon, :type :3planet, :image "3planet/Tile-Ashtroth-Loki-Abaddon.gif"
    :planets {:ashtroth {:res 2 :inf 0 :loc [0 -50]}
              :loki {:res 1 :inf 2 :loc [50 50]}
              :abaddon {:res 1 :inf 0 :tech {:red 1} :loc [-50 -50]}}}
   {:id :rigel, :type :3planet, :image "3planet/Tile-Rigel.gif"
    :planets {:rigel-i {:res 0 :inf 1 :tech {:green 1} :loc [0 -50]}
              :rigel-ii {:res 1 :inf 2 :loc [50 50]}
              :rigel-iii {:res 1 :inf 1 :tech {:blue 1} :loc [-50 -50]}}}
   {:id :elder-uhuru-amani, :type :3planet, :image "3planet/Tile-Elder-Uhuru-Amani.gif"
    :planets {:elder-one {:res 0 :inf 0 :tech {:green 3} :loc [0 -50]}
              :uhuru {:res 1 :inf 0 :loc [-50 60]}
              :amani {:res 0 :inf 1 :loc [50 50]}}}
   {:id :tianshang-tiangu-changtian, :type :3planet, :image "3planet/Tile-Tianshang-Tiangu-Changtian.gif"
    :planets {:tianshang {:res 1 :inf 1 :loc [50 -50]}
              :tiangu-xing {:res 1 :inf 1 :loc [-70 0]}
              :changtian {:res 2 :inf 0 :special "Exhaust for 2 TG" :loc [50 50]}}}])

(def home-systems-arr
  [
   ; Regular homesystems
   {:id :arborec :type :home-system :image "HomeSystem/Tile-HS-Arborec.gif"}
   {:id :creuss :type :home-system :image "HomeSystem/Tile-HS-Creuss.gif"}
   {:id :creuss-gate :type :home-system :image "HomeSystem/Tile-HS-Creuss_Gate.gif"}
   {:id :hacan :type :home-system :image "HomeSystem/Tile-HS-Hacan.gif"}
   {:id :jolnar :type :home-system :image "HomeSystem/Tile-HS-Jolnar.gif"}
   {:id :lisix :type :home-system :image "HomeSystem/Tile-HS-L1z1x.gif"}
   {:id :mentak :type :home-system :image "HomeSystem/Tile-HS-Mentak.gif"}
   {:id :lazax :type :home-system :image "HomeSystem/Tile-HS-Lazax.gif"}
   {:id :letnev :type :home-system :image "HomeSystem/Tile-HS-Letnev.gif"}
   {:id :winnu :type :home-system :image "HomeSystem/Tile-HS-Winnu.gif"}
   {:id :muaat :type :home-system :image "HomeSystem/Tile-HS-Muaat.gif"}
   {:id :naalu :type :home-system :image "HomeSystem/Tile-HS-Naalu.gif"}
   {:id :nekro :type :home-system :image "HomeSystem/Tile-HS-Nekro.gif"}
   {:id :sol :type :home-system :image "HomeSystem/Tile-HS-Sol.gif"}
   {:id :xxcha :type :home-system :image "HomeSystem/Tile-HS-Xxcha.gif"}
   {:id :yin :type :home-system :image "HomeSystem/Tile-HS-Yin.gif"}
   {:id :yssaril :type :home-system :image "HomeSystem/Tile-HS-Yssaril.gif"}
   {:id :saar :type :home-system :image "HomeSystem/Tile-HS-Saar.gif"
    :planets {:lisis2 {:res 1 :inf 0} :ragh {:res 2 :inf 1}}}

   ; Community race homesystems
   {:id :akoytay :type :home-system :image "HomeSystem/Tile-HS-Akoytay.gif"}
   {:id :alkari :type :home-system :image "HomeSystem/Tile-HS-Alkari.gif"}
   {:id :alliance :type :home-system :image "HomeSystem/Tile-HS-Alliance.gif"}
   {:id :altair :type :home-system :image "HomeSystem/Tile-HS-Altair.gif"}
   {:id :andorian :type :home-system :image "HomeSystem/Tile-HS-Andorian.gif"}
   {:id :asari :type :home-system :image "HomeSystem/Tile-HS-Asari.gif"}
   {:id :asgard :type :home-system :image "HomeSystem/Tile-HS-Asgard.gif"}
   {:id :atreides :type :home-system :image "HomeSystem/Tile-HS-Atreides.gif"}
   {:id :bajoran :type :home-system :image "HomeSystem/Tile-HS-Bajoran.gif"}
   {:id :batarian :type :home-system :image "HomeSystem/Tile-HS-Batarian.gif"}
   {:id :bene-gesserit :type :home-system :image "HomeSystem/Tile-HS-Bene_Gesserit.gif"}
   {:id :bene-tleilaxu :type :home-system :image "HomeSystem/Tile-HS-Bene_Tleilaxu.gif"}
   {:id :betazoid :type :home-system :image "HomeSystem/Tile-HS-Betazoid.gif"}
   {:id :bolian :type :home-system :image "HomeSystem/Tile-HS-Bolian.gif"}
   {:id :bulrathi :type :home-system :image "HomeSystem/Tile-HS-Bulrathi.gif"}
   {:id :caitian :type :home-system :image "HomeSystem/Tile-HS-Caitian.gif"}
   {:id :cannibals :type :home-system :image "HomeSystem/Tile-HS-Cannibals.gif"}
   {:id :cardassian :type :home-system :image "HomeSystem/Tile-HS-Cardassian.gif"}
   {:id :cartel :type :home-system :image "HomeSystem/Tile-HS-Cartel.gif"}
   {:id :centauri :type :home-system :image "HomeSystem/Tile-HS-Centauri.gif"}
   {:id :chaos :type :home-system :image "HomeSystem/Tile-HS-Chaos.gif"}
   {:id :chromatics :type :home-system :image "HomeSystem/Tile-HS-Chromatics.gif"}
   {:id :corrino :type :home-system :image "HomeSystem/Tile-HS-Corrino.gif"}
   {:id :croax :type :home-system :image "HomeSystem/Tile-HS-Croax.gif"}
   {:id :dark-eldar :type :home-system :image "HomeSystem/Tile-HS-DarkEldar.gif"}
   {:id :darlocks :type :home-system :image "HomeSystem/Tile-HS-Darlocks.gif"}
   {:id :dominion :type :home-system :image "HomeSystem/Tile-HS-Dominion.gif"}
   {:id :dusun :type :home-system :image "HomeSystem/Tile-HS-Dusun.gif"}
   {:id :earth :type :home-system :image "HomeSystem/Tile-HS-Earth.gif"}
   {:id :eldar :type :home-system :image "HomeSystem/Tile-HS-Eldar.gif"}
   {:id :elerians :type :home-system :image "HomeSystem/Tile-HS-Elerians.gif"}
   {:id :elzenkar :type :home-system :image "HomeSystem/Tile-HS-Elzenkar.gif"}
   {:id :embers :type :home-system :image "HomeSystem/Tile-HS-Embers.gif"}
   {:id :fedaron :type :home-system :image "HomeSystem/Tile-HS-Fedaron.gif"}
   {:id :federation :type :home-system :image "HomeSystem/Tile-HS-Federation.gif"}
   {:id :ferengi :type :home-system :image "HomeSystem/Tile-HS-Ferengi.gif"}
   {:id :firijii :type :home-system :image "HomeSystem/Tile-HS-Firijii.gif"}
   {:id :geth :type :home-system :image "HomeSystem/Tile-HS-Geth.gif"}
   {:id :gnolams :type :home-system :image "HomeSystem/Tile-HS-Gnolams.gif"}
   {:id :goauld :type :home-system :image "HomeSystem/Tile-HS-Goauld.gif"}
   {:id :harkonnen :type :home-system :image "HomeSystem/Tile-HS-Harkonnen.gif"}
   {:id :hirogen :type :home-system :image "HomeSystem/Tile-HS-Hirogen.gif"}
   {:id :humans :type :home-system :image "HomeSystem/Tile-HS-Humans.gif"}
   {:id :imperium-of-man :type :home-system :image "HomeSystem/Tile-HS-Imperium_of_Man.gif"}
   {:id :ironleague :type :home-system :image "HomeSystem/Tile-HS-Ironleague.gif"}
   {:id :jipa :type :home-system :image "HomeSystem/Tile-HS-Jipa.gif"}
   {:id :kazon :type :home-system :image "HomeSystem/Tile-HS-Kazon.gif"}
   {:id :klackon :type :home-system :image "HomeSystem/Tile-HS-Klackon.gif"}
   {:id :klingon :type :home-system :image "HomeSystem/Tile-HS-Klingon.gif"}
   {:id :krenim :type :home-system :image "HomeSystem/Tile-HS-Krenim.gif"}
   {:id :krogan :type :home-system :image "HomeSystem/Tile-HS-Krogan.gif"}
   {:id :lun :type :home-system :image "HomeSystem/Tile-HS-Lun.gif"}
   {:id :mahact-sodality :type :home-system :image "HomeSystem/Tile-HS-Mahact_Sodality.gif"}
   {:id :maquis :type :home-system :image "HomeSystem/Tile-HS-Maquis.gif"}
   {:id :meklars :type :home-system :image "HomeSystem/Tile-HS-Meklars.gif"}
   {:id :minbari :type :home-system :image "HomeSystem/Tile-HS-Minbari.gif"}
   {:id :mirssen :type :home-system :image "HomeSystem/Tile-HS-Mirssen.gif"}
   {:id :moritani :type :home-system :image "HomeSystem/Tile-HS-Moritani.gif"}
   {:id :mrrshan :type :home-system :image "HomeSystem/Tile-HS-Mrrshan.gif"}
   {:id :mystics :type :home-system :image "HomeSystem/Tile-HS-Mystics.gif"}
   {:id :narn :type :home-system :image "HomeSystem/Tile-HS-Narn.gif"}
   {:id :necron :type :home-system :image "HomeSystem/Tile-HS-Necron.gif"}
   {:id :norr :type :home-system :image "HomeSystem/Tile-HS-Norr.gif"}
   {:id :omohry :type :home-system :image "HomeSystem/Tile-HS-Omohry.gif"}
   {:id :ori :type :home-system :image "HomeSystem/Tile-HS-Ori.gif"}
   {:id :orion :type :home-system :image "HomeSystem/Tile-HS-Orion.gif"}
   {:id :orks :type :home-system :image "HomeSystem/Tile-HS-Orks.gif"}
   {:id :overmind :type :home-system :image "HomeSystem/Tile-HS-Overmind.gif"}
   {:id :paradisian :type :home-system :image "HomeSystem/Tile-HS-Paradisian.gif"}
   {:id :protoss :type :home-system :image "HomeSystem/Tile-HS-Protoss.gif"}
   {:id :psilons :type :home-system :image "HomeSystem/Tile-HS-Psilons.gif"}
   {:id :qikai :type :home-system :image "HomeSystem/Tile-HS-Qikai.gif"}
   {:id :raiders :type :home-system :image "HomeSystem/Tile-HS-Raiders.gif"}
   {:id :renegade :type :home-system :image "HomeSystem/Tile-HS-Renegade.gif"}
   {:id :replicator :type :home-system :image "HomeSystem/Tile-HS-Replicator.gif"}
   {:id :richese :type :home-system :image "HomeSystem/Tile-HS-Richese.gif"}
   {:id :romulan :type :home-system :image "HomeSystem/Tile-HS-Romulan.gif"}
   {:id :rrargan :type :home-system :image "HomeSystem/Tile-HS-Rrargan.gif"}
   {:id :sakkra :type :home-system :image "HomeSystem/Tile-HS-Sakkra.gif"}
   {:id :salarian :type :home-system :image "HomeSystem/Tile-HS-Salarian.gif"}
   {:id :shabbak :type :home-system :image "HomeSystem/Tile-HS-Shabbak.gif"}
   {:id :shadows :type :home-system :image "HomeSystem/Tile-HS-Shadows.gif"}
   {:id :silicoids :type :home-system :image "HomeSystem/Tile-HS-Silicoids.gif"}
   {:id :sirkaan1 :type :home-system :image "HomeSystem/Tile-HS-Sirkaan_1.gif"}
   {:id :sirkaan2 :type :home-system :image "HomeSystem/Tile-HS-Sirkaan_2.gif"}
   {:id :slavers :type :home-system :image "HomeSystem/Tile-HS-Slavers.gif"}
   {:id :sturmgard :type :home-system :image "HomeSystem/Tile-HS-Sturmgard.gif"}
   {:id :swarm :type :home-system :image "HomeSystem/Tile-HS-Swarm.gif"}
   {:id :systems-alliance :type :home-system :image "HomeSystem/Tile-HS-SystemsAlliance.gif"}
   {:id :tau :type :home-system :image "HomeSystem/Tile-HS-Tau.gif"}
   {:id :tauri :type :home-system :image "HomeSystem/Tile-HS-Tauri.gif"}
   {:id :tellarite :type :home-system :image "HomeSystem/Tile-HS-Tellarite.gif"}
   {:id :terran :type :home-system :image "HomeSystem/Tile-HS-Terran.gif"}
   {:id :tlet :type :home-system :image "HomeSystem/Tile-HS-Tlet.gif"}
   {:id :tokra :type :home-system :image "HomeSystem/Tile-HS-Tokra.gif"}
   {:id :traelyn :type :home-system :image "HomeSystem/Tile-HS-Traelyn.gif"}
   {:id :triacterial :type :home-system :image "HomeSystem/Tile-HS-Triacterial.gif"}
   {:id :trilarians :type :home-system :image "HomeSystem/Tile-HS-Trilarians.gif"}
   {:id :trill :type :home-system :image "HomeSystem/Tile-HS-Trill.gif"}
   {:id :trillarians :type :home-system :image "HomeSystem/Tile-HS-Trillarians.gif"}
   {:id :turian :type :home-system :image "HomeSystem/Tile-HS-Turian.gif"}
   {:id :tyranid :type :home-system :image "HomeSystem/Tile-HS-Tyranid.gif"}
   {:id :vernius :type :home-system :image "HomeSystem/Tile-HS-Vernius.gif"}
   {:id :vorlon :type :home-system :image "HomeSystem/Tile-HS-Vorlon.gif"}
   {:id :vulcan :type :home-system :image "HomeSystem/Tile-HS-Vulcan.gif"}
   {:id :xel-naga :type :home-system :image "HomeSystem/Tile-HS-XelNaga.gif"}
   {:id :xindi :type :home-system :image "HomeSystem/Tile-HS-Xindi.gif"}
   {:id :zzedajin :type :home-system :image "HomeSystem/Tile-HS-Zzedajin.gif"}])

(def special-systems-arr
  [{:id :empty, :type :special, :image "Special/Tile-Empty.gif"}
   {:id :wormhole-a, :type :special, :image "Special/Tile-Wormhole_A.gif"}
   {:id :wormhole-b, :type :special, :image "Special/Tile-Wormhole_B.gif"}
   {:id :asteroid-field, :type :special,
    :image "Special/Tile-Asteroid_Field.gif"
    :description ["Need Anti-mass deflector tech to move through"
                  "Ship may not end movement in Asteroid Field"
                  "Cannot be activated"]}
   {:id :ion-storm, :type :special, :image "Special/Tile-Ion_Storm.gif"
    :description ["Ships may not move through Ion Storm"
                  "Ships may move to Ion Storm"
                  "PDS cannot fire to ships in Ion Storm"
                  "Fighters do not shoot in Ion Storm but may be taken as casualties"]}
   {:id :nebula, :type :special, :image "Special/Tile-Nebula.gif"
    :description ["Ships may not move through Nebula"
                  "Ships may move to Nebula"
                  "Defender gets +1 combat in space battle"
                  "Ship leaving Nebula always has its movement reduced to 1"]}
   {:id :supernova, :type :special, :image "Special/Tile-Supernova.gif"
    :description ["Supernova is impassible"
                  "Supernova may never be activated."]}
   {:id :gravity-rift, :type :special, :image "Special/Tile-Gravity_rift.gif"}
   {:id :ancient-minefield, :type :special, :image "Special/Tile-Ancient_Minefield.gif"}
   {:id :galactic-storm, :type :special, :image "Special/Tile-Galactic_Storm.gif"}
   {:id :gravity-well, :type :special, :image "Special/Tile-Gravity_Well.gif"}
   {:id :black-hole, :type :special, :image "Special/Tile-BlackHole.gif"}
   {:id :pulsar, :type :special, :image "Special/Tile-Pulsar.gif"}
   {:id :quantum-singularity, :type :special, :image "Special/Tile-Quantum_Singularity.gif"}])

(def king-systems-arr
  [; Only one of these to the center
   {:id :babylon-5, :type :center-system, :image "Special/Tile-Babylon_5.gif" :planets {:babylon-5 {:res 1 :inf 6 :loc [0 0]}}}
   {:id :citadel, :type :center-system, :image "Special/Tile-Citadel.gif" :planets {:citadel {:res 1 :inf 6 :loc [0 0]}}}
   {:id :mecatol-rex, :type :center-system, :image "Special/Tile-Mecatol_Rex.gif" :planets {:mecatol-rex {:res 1 :inf 6 :loc [0 0]}}}
   {:id :mecatol-rex09, :type :center-system, :image "Special/Tile-Mecatol_Rex09.gif" :planets {:mecatol-rex {:res 0 :inf 9 :loc [0 0]}}}
   {:id :old-mecatol-rex, :type :center-system, :image "Special/Tile-OldMecatolRex.gif" :planets {:mecatol-rex {:res 4 :inf 10 :loc [0 0]}}}
   {:id :orion2, :type :center-system, :image "Special/Tile-Orion2.gif" :planets {:orion {:res 4 :inf 6 :loc [0 0]}}}
   {:id :dune, :type :center-system, :image "1planet/Tile-Dune.gif" :planets {:dune {:res 0 :inf 4 :tech {:green 1} :loc [0 0]}}}
   {:id :medusa-v, :type :center-system, :image "1planet/Tile-Medusa_V.gif" :planets {:medusa-v {:res 1 :inf 6 :loc [0 0]}}}])

(def setup-systems-arr
  [{:id :setup-dark-blue, :type :setup, :image "Setup/Tile-Setup-DarkBlue.gif"}
   {:id :setup-light-blue, :type :setup, :image "Setup/Tile-Setup-LightBlue.gif"}
   {:id :setup-medium-blue, :type :setup, :image "Setup/Tile-Setup-MediumBlue.gif"}
   {:id :setup-red, :type :setup, :image "Setup/Tile-Setup-Red.gif"}
   {:id :setup-yellow, :type :setup, :image "Setup/Tile-Setup-Yellow.gif"}
   {:id :hs-back, :type :setup, :image "HomeSystem/Tile-HS-Back.gif"}])

(def all-systems-list
  (concat
    planet-systems-arr
    home-systems-arr
    special-systems-arr
    king-systems-arr
    setup-systems-arr))

(def all-systems (index-by :id all-systems-list))
