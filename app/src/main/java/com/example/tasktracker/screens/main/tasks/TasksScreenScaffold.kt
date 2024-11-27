package com.example.tasktracker.screens.main.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ravenzip.workshop.components.SimpleButton
import com.ravenzip.workshop.components.SinglenessTextField
import com.ravenzip.workshop.components.TopAppBar
import com.ravenzip.workshop.data.TextConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenScaffold(
    padding: PaddingValues,
    vararg onClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val taskName = remember { mutableStateOf("") }
    val taskValue = remember { mutableStateOf("") }
    val taskCode = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = { TopAppBar(title = "Задачи", backArrow = null, items = listOf()) },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) { innerPadding ->
        TasksScreen(
            padding = innerPadding,
            onClick = onClick,
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.heightIn(screenHeight * 0.8f),
                onDismissRequest = {
                    showBottomSheet = false
                    taskName.value = ""
                    taskValue.value = ""
                    taskCode.value = ""
                },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Создание задачи",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SinglenessTextField(text = taskName, label = "Наименование задачи")

                    Spacer(modifier = Modifier.height(5.dp))

                    SinglenessTextField(text = taskValue, label = "Статус задачи")

                    Spacer(modifier = Modifier.height(5.dp))

                    SinglenessTextField(text = taskCode, label = "Исполнитель")

                    Spacer(modifier = Modifier.height(20.dp))

                    SimpleButton(
                        text = "Создать",
                        textConfig = TextConfig(size = 19.sp, align = TextAlign.Center)
                    ) {}
                }
            }
        }
    }
}
