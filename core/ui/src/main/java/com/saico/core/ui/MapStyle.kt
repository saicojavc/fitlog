package com.saico.core.ui

object MapStyle {
    // Este JSON define los colores oscuros/azulados del mapa
    val JSON = """
    [
      { "elementType": "geometry", "stylers": [ { "color": "#0d1424" } ] },
      { "elementType": "labels.text.fill", "stylers": [ { "color": "#3fb9f6" } ] },
      { "featureType": "road", "elementType": "geometry", "stylers": [ { "color": "#1e293b" } ] },
      { "featureType": "water", "elementType": "geometry", "stylers": [ { "color": "#080e1e" } ] },
      { "featureType": "poi", "stylers": [ { "visibility": "off" } ] }
    ]
    """.trimIndent()
}