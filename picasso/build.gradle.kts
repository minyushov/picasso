plugins {
  picasso
}

dependencies {
  api(deps.okhttp)
  compileOnly(deps.annotations)
  implementation(deps.exif)
  testImplementation(deps.junit)
  testImplementation(deps.truth)
  testImplementation(deps.robolectric)
  testImplementation(deps.mockito)
  testImplementation(deps.mockWebServer)
}