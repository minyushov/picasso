plugins {
  picasso
}

dependencies {
  api(project(":picasso"))
  api(deps.pollexor)
  compileOnly(deps.annotations)
  testImplementation(deps.junit)
  testImplementation(deps.robolectric)
  testImplementation(deps.truth)
  testImplementation(deps.pollexor)
}