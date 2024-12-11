package com.example.tasktracker.screens.userProfile.infoUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktracker.data.Company
import com.example.tasktracker.data.User
import com.example.tasktracker.viewModels.InfoUserViewModel
import com.ravenzip.workshop.components.RowIconButton
import com.ravenzip.workshop.components.Switch
import com.ravenzip.workshop.data.TextConfig
import com.ravenzip.workshop.data.icon.Icon
import kotlinx.coroutines.launch

@Composable
fun InfoUserScreen(
    padding: PaddingValues,
    infoUserViewModel: InfoUserViewModel,
    navigateToCompany: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val user = infoUserViewModel.dataCurrentUser.collectAsState().value

    val dataUser = infoUserViewModel.dataUser.collectAsState().value
    val dataCompany = infoUserViewModel.dataCompany.collectAsState().value

    val onEditSwitch = remember(user) { mutableStateOf(user.onEdit) }
    val onDeleteSwitch = remember(user) { mutableStateOf(user.onDelete) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { UserCard(user, dataCompany) }

        if (dataUser.id == dataCompany.creatorId && dataCompany.creatorId != user.id) {
            item {
                Text(
                    text = "Настройка прав",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            item {
                Switch(
                    isChecked = onEditSwitch,
                    title = "Право редактирования",
                    titleConfig = TextConfig.Normal,
                    text = "Разрешить редактирование задач",
                    textConfig = TextConfig.Small,
                    onCheckedChanged = {
                        scope.launch {
                            val newUser =
                                User(
                                    id = user.id,
                                    name = user.name,
                                    surname = user.surname,
                                    companyId = user.companyId,
                                    lastTaskViewId = user.lastTaskViewId,
                                    onEdit = !onEditSwitch.value,
                                    onDelete = user.onDelete,
                                    tasks = user.tasks
                                )
                            infoUserViewModel.updateOnEdit(newUser, !onEditSwitch.value)
                            infoUserViewModel.updateCurrentUser(newUser)
                            onEditSwitch.value = !onEditSwitch.value
                        }
                    }
                )
            }

            item {
                Switch(
                    isChecked = onDeleteSwitch,
                    title = "Право удаления",
                    titleConfig = TextConfig.Normal,
                    text = "Разрешить удаление задач",
                    textConfig = TextConfig.Small,
                    onCheckedChanged = {
                        scope.launch {
                            val newUser =
                                User(
                                    id = user.id,
                                    name = user.name,
                                    surname = user.surname,
                                    companyId = user.companyId,
                                    lastTaskViewId = user.lastTaskViewId,
                                    onEdit = user.onEdit,
                                    onDelete = !onDeleteSwitch.value,
                                    tasks = user.tasks
                                )
                            infoUserViewModel.updateOnDelete(newUser, !onDeleteSwitch.value)
                            infoUserViewModel.updateCurrentUser(newUser)
                            onDeleteSwitch.value = !onDeleteSwitch.value
                        }
                    }
                )
            }

            item {
                Text(
                    text = "Управление пользователем",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            item {
                RowIconButton(
                    text = "Исключить из организации",
                    textConfig = TextConfig(size = 19.sp),
                    icon = Icon.ImageVectorIcon(Icons.Outlined.PersonRemove),
                ) {
                    scope.launch {
                        infoUserViewModel.deleteCurrentUserInCompany(user)
                        navigateToCompany()
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// Карточка для отображения информации о сотруднике
@Composable
fun UserCard(user: User, currentCompany: Company) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Заголовок
            Text(
                text = "Информация о сотруднике",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Данные о сотруднике
            UserDetailItem(label = "Сотрудник:", value = user.name + " " + user.surname)
            UserDetailItem(label = "Компания:", value = currentCompany.name)
            UserDetailItem(
                label = "Должность:",
                value = if (user.id != currentCompany.creatorId) "Сотрудник" else "Директор"
            )
            UserDetailItem(
                label = "Право на редактирование задач:",
                value = if (user.onEdit) "Да" else "Нет"
            )
            UserDetailItem(
                label = "Право на удвление задач:",
                value = if (user.onDelete) "Да" else "Нет"
            )
        }
    }
}

// Вспомогательный компонент для строки данных
@Composable
fun UserDetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
