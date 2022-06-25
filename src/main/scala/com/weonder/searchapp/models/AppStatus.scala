package com.weonder.searchapp.models

import derevo.circe.magnolia.encoder
import derevo.derive

@derive(encoder)
case class AppStatus(appStatus: Boolean)
