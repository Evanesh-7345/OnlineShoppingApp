package com.shoppy.shopkart.screens.admin.orderstatus

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.shoppy.shopkart.R
import com.shoppy.shopkart.ShopKartUtils
import com.shoppy.shopkart.components.BackButton
import com.shoppy.shopkart.components.DeliveryStatusCard
import com.shoppy.shopkart.components.PillButton
import com.shoppy.shopkart.models.MOrder
import com.shoppy.shopkart.models.NotificationData
import com.shoppy.shopkart.models.PushNotificationData
import com.shoppy.shopkart.navigation.BottomNavScreens
import com.shoppy.shopkart.screens.search.SearchBox

@Composable
fun OrderedItems(navHostController: NavHostController,viewModel: OrderStatusViewModel = hiltViewModel()){

    val orderedItemsList = remember { mutableStateOf(emptyList<MOrder>()) }

    val searchByOrderId = remember { mutableStateOf("") }

    val title = remember { mutableStateOf("Delivered") }
    val message = remember { mutableStateOf("Your Item is delivered") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    orderedItemsList.value = viewModel.fireStatus.value.data?.toList()?.filter { mOrder ->

        mOrder.delivery_status == "Ordered"

    }!!

    FirebaseMessaging.getInstance().subscribeToTopic(ShopKartUtils.TOPIC)

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = { BackButton(navController = navHostController, topBarTitle = "Ordered Items", spacing = 50.dp) }, backgroundColor = ShopKartUtils.offWhite) { innerPadding ->

        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                SearchBox(value = searchByOrderId.value, onChange = searchByOrderId, leadingIcon = R.drawable.ic_search, placeHolder = "Search by Order Id", customAutoFocus = false)
                //Search Button
                IconButton(modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                    onClick = {
                        orderedItemsList.value = viewModel.fireStatus.value.data?.toList()?.filter { mOrder ->

                        mOrder.order_id == searchByOrderId.value
                    }!!
                    }){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }

            LazyColumn{
                items(items = orderedItemsList.value){ ordered ->
                    DeliveryStatusCard(ordered = ordered, buttonTitle = "Mark On The Way", navHostController = navHostController){
                        viewModel.markOnTheWay(
                            userId = ordered.user_id!!,
                            product_title = ordered.product_title!!
                        ) {
                            navHostController.popBackStack()
                            navHostController.navigate(BottomNavScreens.OrderedItems.route)
                            Toast.makeText(context, "Item marked as On The Way", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }
}


