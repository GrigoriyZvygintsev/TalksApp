package com.gzvyagintsev.talks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gzvyagintsev.talks.data.model.Talk
import com.gzvyagintsev.talks.ui.theme.CardColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary

@Composable
fun TalkCard(talk: Talk, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("card_talk_${talk.slug}")
            .semantics { contentDescription = "card_talk_${talk.slug}" },
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LevelBadge(level = talk.level, slug = talk.slug)
                Text(
                    text = talk.duration,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.testTag("text_duration_${talk.slug}")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = talk.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.testTag("text_talk_title_${talk.slug}")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = talk.summary,
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.testTag("text_talk_summary_${talk.slug}")
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(talk.tags) { tag -> TagChip(tag = tag) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = talk.date,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("text_date_${talk.slug}")
            )
        }
    }
}
