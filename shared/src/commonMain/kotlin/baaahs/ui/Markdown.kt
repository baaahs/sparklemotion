package baaahs.ui

/** Specifies that the annotated [String] property will be formatted as markdown. */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class Markdown