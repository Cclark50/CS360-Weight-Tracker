package com.snhu.ProjectTwo.utilities

// Float extension that checks if our float is within the acceptable range
// Const values let us change all instances of the upper and lower bounds from one location instead of
// Needing to change it in every location they're referenced in
private const val HIGHEST_WEIGHT: Float = 3000f
private const val LOWEST_WEIGHT: Float = 0.1f
fun Float.isValidWeight(): Boolean = this in LOWEST_WEIGHT..HIGHEST_WEIGHT