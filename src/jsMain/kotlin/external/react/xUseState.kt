@file:JsModule("react")
@file:JsNonModule

package external.react

import react.RDependenciesArray

///**
// * Only works inside [fc]
// * @see <a href="https://reactjs.org/docs/hooks-state.html#hooks-and-function-components">Hooks and Function Components</a>
// */
//external fun <T : Any> useState(): StateInstance<T?>
//
///**
// * Only works inside [fc]
// * @see <a href="https://reactjs.org/docs/hooks-state.html#hooks-and-function-components">Hooks and Function Components</a>
// */
//external fun <T> useState(
//    initialValue: T,
//): StateInstance<T>

/**
 * Only works inside [fc]
 * @see <a href="https://reactjs.org/docs/hooks-state.html#hooks-and-function-components">Hooks and Function Components</a>
 */
@JsName("useState")
external fun <T> rawUseState(initValue: T): RDependenciesArray
//external fun <T> rawUseState(
//    initializer: () -> T,
//): XStateInstance<T>
