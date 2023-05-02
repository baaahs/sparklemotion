*This file contains notes related to adding support for sparklehenge. In general these are personal and incomplete reflecting an my current incomplete understanding. I'd recommend you ignore them, but hey, do what you want!*

In baaahs.visualizer VisualizerBuilder I think there needs to be a getHengeEditor implementation

The PixelArrayVisualizer in LightBarVisualizer.kt seems like it can do an arbitrary pixel array


`HengeEditorView` defines UI for editing `MutableHengeData`

`MutableHengeData` is in MutableScene.ht and it would have data about the Henge. For a lightbar it is start / end / length. For a grid `MutableGridData` it is rows, columns, gaps, etc.

sparklemotion.commonMain
------------------------

* baaahs.model -> ModelData.kt 
Contains the data objects for things in the scene. This is where `HengeData` is defined alongside things like `GridData`

* baaahs.model -> PolyLine.kt
Contains the class definition for a `Henge` which is a sub-class of PolyLine. The definition of parameters is done in ModelData.kt and then this file uses the defined data to calculate a PolyLine with segments based on the defining aspects of the Henge data as stored in the `HengeData` model. This is the important place where since this is a PolyLine we don't have to build other vizualizers.

* baaahs.models
A class can be defined here to define a scene in code - or a more complex model with multiple Henge's perhaps? Or is a Henge a collection of LightBars????

* baaahs.plugin -> Plugins.kt
There is a registration of serializers. One has to be defined for HengeData following the model of the others of type EntityData

* baaahs.scene -> EntityEditorPanel.kt
Defines objects including a `HengeEditorPanel` which extend `EntityEditorPanel`. These things all delegate to an appropriate function on visualizerBuilder such as `getHengeEditorView` which return a View for a given EditingEntity

* bsaahs.scene -> MutableScene.kt
Defines mutable classes for the entity data such as `MutableHengeData` This mutable data should match up with what is in ModelData.kt

* baaahs.sim
There is a LightBarSimulation class in here that maybe we need to copy something like this? However I think we are trying to be a PolyLine descendent like Grid, which then doesn't have its own simulation??? **Re-examine this**

* baaahs.visualizer -> EntityAdapter.kt
Again there are createXXXVisualizer calls for entities but not for Grid, so I think we just need to make sure the createPolyLineVisualizer is called properly somewhere for our Henge **Re-examine this**

* baaahs.visualizer -> ItemVisualizer.kt
Contains the definition of VisualizerBuilder which requires getXXXXView methods as mentioned above. This is the interface definition for VisualizerBuilder which has a concrete implementation in jsMain

sparklemotion.jsMain
--------------------

* baaahs.app.ui.model -> EntityTypes.kt
A new entity type needs to be added here so that the list of things which can be added to a scene can be used to add a Henge. The type both has to be declared and it has to be added to the `EntityTypes` val at the top of the file.

* baaahs.app.ui.model -> HengeEditorView / GridEditorView.kt / etc
These files define the actual views that includes parameter entry for all the data that can be edited in the model of a particular type. A UI component is defined along with an interface and a function on RBuilder

* baaahs.sim 
Has concrete classes such as LightBarSimulation.kt that we might need??? **Re-examine this**

* baaahs.visualizer -> EntityAdapter.kt
Concrete implementations of the functions defined in the commonMain. Since there isn't a GridVisualizer here we're still going with the idea we don't need one for Henge.

baaahs.visualizer -> EntityVisualizer.kt
The VisualizerBuilder defined here is an implementation of the interface defined in ItemVisualizer.kt that is mentioned above. As such adding a method to that interface requires an implementaiton be added here. In particular the name defined here needs to match up with the function defined on RBuilder from `HengeEditorView` for example.


A Henge is a fixture of type PixelArrayDevice so in terms of visualization etc. we don't have to define a new vizualizer.

