package com.owly.owlyandroidapp

import com.owly.owlyandroidapp.bean.Item

object ItemsSingleton {

  val item1 = Item("Bycicle","Hey","https://www.bikelec.es/media/catalog/product/b/i/bicicleta_bulls_e-steam-evo-3-27.5.jpg",299.0)
  val item2 = Item("Iphone 6 Plus S","Hello","http://www.nosoloios.com/imagenes/iPhone-6-Plus-vs-iPhone-6S-Plus-700x400.jpg",399.0)
  val item3 = Item("LG 4k Edition","Hello","https://i.sdlcdn.com/img/product/descimg/SDL655020775_1.jpg",50000.0)
  val item4 = Item("PS4 White Edition","Hello","http://cms.centroone.com/contents/News/18096/18096_original.jpg",280.0)
  val item5 = Item("Tesla S Model","Hello","https://i.pinimg.com/originals/cf/99/14/cf99147c6f6bfdd457bc4b860ca1f99e.jpg",450.0)
  val item6 = Item("Bosch Serie 8 Active Oxygen","Hello","http://sphm-sww-site-production.s3-ap-southeast-1.amazonaws.com/2017/09/A-List-Awards-Bosch-Series-8-ActiveOxygen-Washer-WAW28790IL_NEW.jpg",0.0)

  val ITEMS = arrayListOf<Item>(item1, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6, item2, item3,item4,item5,item6)
}