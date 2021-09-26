package dev.sebastiano.bundel.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sebastiano.bundel.composables.checkedMaterialPillAppearance
import dev.sebastiano.bundel.onboarding.TimeRangeRow
import dev.sebastiano.bundel.preferences.schedule.TimeRangesSchedule
import dev.sebastiano.bundel.ui.regularThemeMaterialChipBackgroundColor
import dev.sebastiano.bundel.ui.regularThemeMaterialChipContentColor
import dev.sebastiano.bundel.ui.singlePadding

@Composable
internal fun SelectTimeRangesDialog(
    viewModel: EnglebertViewModel = hiltViewModel(),
    onDialogDismiss: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Choose the hours on which Bundel will be active.")

            val timeRangesSchedule by viewModel.timeRangesScheduleFlow.collectAsState(initial = TimeRangesSchedule())

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(singlePadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RangesListContent(timeRangesSchedule, viewModel)
            }

            TextButton(onClick = onDialogDismiss, modifier = Modifier.align(Alignment.End)) {
                Text(text = "DONE")
            }
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.RangesListContent(
    timeRangesSchedule: TimeRangesSchedule,
    viewModel: EnglebertViewModel
) {
    val items = timeRangesSchedule.timeRanges.withIndex().toList()

    items(items = items) { (index, timeRange) ->
        val minimumAllowedFrom = if (index > 0) items[index - 1].value.to else null
        val maximumAllowedTo = if (index < items.count() - 1) items[index + 1].value.from else null

        TimeRangeRow(
            expandedPillAppearance = checkedMaterialPillAppearance(
                backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(true),
                contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(true)
            ),
            normalPillAppearance = checkedMaterialPillAppearance(
                backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(false),
                contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(false)
            ),
            timeRange = timeRange,
            canBeRemoved = timeRangesSchedule.canRemoveRanges,
            minimumAllowableFrom = minimumAllowedFrom,
            maximumAllowableTo = maximumAllowedTo,
            onRemoved = if (timeRangesSchedule.canRemoveRanges) {
                { viewModel.onTimeRangesScheduleRemoveTimeRange(timeRange) }
            } else {
                { }
            }
        ) { newTimeRange -> viewModel.onTimeRangesScheduleChangeTimeRange(timeRange, newTimeRange) }
    }

    if (timeRangesSchedule.canAppendAnotherRange) {
        item {
            TimeRangeRow(
                modifier = Modifier
                    .clickable { viewModel.onTimeRangesScheduleAddTimeRange() }
                    .padding(horizontal = 8.dp)
                    .alpha(ContentAlpha.medium),
                normalPillAppearance = checkedMaterialPillAppearance(
                    backgroundColor = MaterialTheme.colors.regularThemeMaterialChipBackgroundColor(false),
                    contentColor = MaterialTheme.colors.regularThemeMaterialChipContentColor(false)
                ),
                timeRange = null,
                enabled = false
            )
        }
    }
}
