package com.weonder.searchapp.models

import derevo.cats.show
import derevo.circe.magnolia._
import derevo.derive

@derive(decoder, encoder, show)
case class Product(product_id: String,
                   product_url: String,
                   title: String,
                   categories: List[String],
                   color: String,
                   detail_pane: String,
                   images: List[String],
                   price: String,
                   price_in_basket: String,
                   price_new: String,
                   price_old: String)
