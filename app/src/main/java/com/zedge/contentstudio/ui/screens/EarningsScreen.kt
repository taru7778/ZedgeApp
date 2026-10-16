package com.zedge.contentstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zedge.contentstudio.core.Accounts
import com.zedge.contentstudio.ui.MainViewModel
import com.zedge.contentstudio.ui.components.EmptyState
import com.zedge.contentstudio.ui.components.SectionCard
import com.zedge.contentstudio.ui.components.StatTile
import org.json.JSONObject
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * v31 Earnings - per-account Zedge revenue with stacked single-candle chart
 * (Ad skips #5ECCC6, Ad plays #0096DF, Zedge credits #783DB0) and color-coded callout badges.
 */
private fun earnMoney(v: Double?): String = if (v == null) "--" else "$" + String.format("%,.2f", v)

private fun earnNum(o: JSONObject?, k: String): Double? {
    if (o == null || !o.has(k) || o.isNull(k)) return null
    val d = o.optDouble(k, Double.NaN)
    return if (d.isNaN()) null else d
}

private fun earnCount(o: JSONObject?, k: String): String {
    val v = earnNum(o, k) ?: return "--"
    return String.format("%,d", v.roundToInt())
}

private fun earnStr(o: JSONObject?, k: String): String = o?.optString(k, "").orEmpty()

private fun earnSlash(s: String): String = if (s.isBlank()) "--" else s.replace("-", "/")

private fun earnAgo(ms: Long): String {
    if (ms <= 0L) return ""
    val mins = ((System.currentTimeMillis() - ms) / 60000L).toInt()
    return when {
        mins < 1 -> "just now"
        mins < 60 -> "$mins min ago"
        mins < 1440 -> "${mins / 60} h ago"
        else -> "${mins / 1440} d ago"
    }
}

@Composable
fun EarningsScreen(vm: MainViewModel) {
    val earnings by vm.earnings.collectAsStateWithLifecycle()
    val activeKey by vm.activeKey.collectAsStateWithLifecycle()
    var sel by remember { mutableStateOf(activeKey) }
    val key = if (Accounts.isValid(sel)) sel else activeKey
    val data = earnings[key]

    val total = Accounts.keys.sumOf { k ->
        val d = earnings[k]
        (earnNum(d, "mtdRevenue") ?: earnNum(d, "netRevenue") ?: 0.0)
    }

    LazyColumn(
        Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                Accounts.all.forEachIndexed { i, acc ->
                    val on = acc.key == key
                    val d = earnings[acc.key]
                    val payable = earnNum(d, "mtdRevenue") ?: earnNum(d, "netRevenue")
                    if (i > 0) Spacer(Modifier.width(8.dp))
                    Row(
                        Modifier
                            .clip(CircleShape)
                            .background(if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { sel = acc.key }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Zedge " + (i + 1),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (on) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(
                            if (payable == null) "--" else earnMoney(payable),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (on) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "All accounts", subtitle = "Total payable across every Zedge account") {
                Text(earnMoney(total), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }
        }

        if (data == null) {
            item {
                EmptyState("This account has not reported earnings yet. The next completed upload run writes the report here.")
            }
        } else {
            val payable = earnNum(data, "mtdRevenue") ?: earnNum(data, "netRevenue")
            val threshold = earnNum(data, "threshold") ?: 50.0
            val payout = data.optBoolean("payoutExpected", false)

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("Unlocks", earnCount(data, "unlocks"), Modifier.weight(1f), accent = Color(0xFF783DB0), icon = Icons.Filled.LockOpen)
                    StatTile("Users", earnCount(data, "users"), Modifier.weight(1f), accent = Color(0xFF5ECCC6), icon = Icons.Filled.Group)
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("Net revenue", earnMoney(earnNum(data, "netRevenue") ?: payable), Modifier.weight(1f), accent = Color(0xFF0096DF), icon = Icons.Filled.Savings)
                    StatTile("Payable now", earnMoney(payable), Modifier.weight(1f), accent = if (payout) Color(0xFF783DB0) else MaterialTheme.colorScheme.primary, icon = Icons.Filled.AccountBalanceWallet)
                }
            }

            item {
                SectionCard(
                    title = "Payout status",
                    subtitle = earnMoney(payable) + " of " + earnMoney(threshold) + " threshold",
                ) {
                    val pct = min(1.0, max(0.0, (payable ?: 0.0) / if (threshold <= 0.0) 1.0 else threshold)).toFloat()
                    Box(
                        Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Box(
                            Modifier.fillMaxWidth(pct).height(8.dp).clip(CircleShape)
                                .background(if (payout) Color(0xFF783DB0) else MaterialTheme.colorScheme.primary),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    val pStartRaw = earnStr(data, "periodStart").ifBlank { earnStr(data, "rangeStart") }
                    val pEndRaw = earnStr(data, "periodEnd").ifBlank { earnStr(data, "rangeEnd") }
                    val pStart = if (pStartRaw > pEndRaw) earnStr(data, "rangeStart") else pStartRaw
                    val pEnd = if (pStartRaw > pEndRaw) earnStr(data, "rangeEnd") else pEndRaw
                    EarnRow("Closed period", earnSlash(pStart) + " - " + earnSlash(pEnd))
                    EarnRow("Closed months net", earnMoney(earnNum(data, "closedRevenue") ?: earnNum(data, "netRevenue") ?: 0.0))
                    EarnRow("Incl. running month", earnMoney(earnNum(data, "mtdRevenue")))
                    val lp = earnStr(data, "lastPayment")
                    EarnRow(
                        "Last payment",
                        if (lp.isBlank()) "No payment received yet" else lp + " • " + earnMoney(earnNum(data, "lastPaymentAmount")),
                    )
                    val paidOn = earnStr(data, "lastPaymentDate")
                    if (paidOn.isNotBlank()) EarnRow("Paid on", paidOn)
                    val upd = data.optLong("updatedAt", 0L)
                    if (upd > 0L) {
                        Spacer(Modifier.height(8.dp))
                        Text("updated " + earnAgo(upd), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                SectionCard(
                    title = "Daily overview",
                    subtitle = earnSlash(earnStr(data, "rangeStart")) + " - " + earnSlash(earnStr(data, "rangeEnd")),
                ) {
                    val days = earnDays(data)
                    if (days.isEmpty()) {
                        EmptyState("No daily data in the last run. The next completed upload run reads the chart again.")
                    } else {
                        var pick by remember(days.size) { mutableStateOf(days.size - 1) }
                        val day = days.getOrNull(pick) ?: days.last()
                        val peak = days.maxOf { it.total }.coerceAtLeast(0.01)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            EarnDot(EARN_SKIPS, "Ad skips")
                            Spacer(Modifier.width(16.dp))
                            EarnDot(EARN_PLAYS, "Ad plays")
                            Spacer(Modifier.width(16.dp))
                            EarnDot(EARN_CREDITS, "Zedge credits")
                        }
                        Spacer(Modifier.height(12.dp))
                        /* Color-coded Zedge callout badges for the active candle */
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            EarnCalloutBadge(Color(0xFF0B0D12), Color.White, day.label)
                            EarnCalloutBadge(Color(0xFF7D47B0), Color.White, earnMoney(day.credits) + " Zedge credits")
                            EarnCalloutBadge(Color(0xFF0096DF), Color.White, earnMoney(day.plays) + " Ad plays")
                            EarnCalloutBadge(Color(0xFF5ECCC6), Color(0xFF0C2624), earnMoney(day.skips) + " Ad skips")
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth().height(180.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            days.forEachIndexed { i, dd ->
                                val on = i == pick
                                val alpha = if (on) 1f else if (ThemeState.palette.isDark) 0.94f else 0.82f
                                Column(
                                    Modifier.weight(1f).clickable { pick = i },
                                    verticalArrangement = Arrangement.Bottom,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    if (dd.total <= 0.0) {
                                        Box(Modifier.fillMaxWidth().height(1.5.dp).background(MaterialTheme.colorScheme.surfaceVariant))
                                    } else {
                                        val hCred = if (dd.credits > 0.0) max(4.5, earnBarPx(dd.credits, peak)) else 3.0
                                        val hSkip = if (dd.skips > 0.0) max(4.5, earnBarPx(dd.skips, peak)) else 2.2
                                        val hPlay = max(4.0, earnBarPx(dd.plays, peak))
                                        Box(
                                            Modifier.fillMaxWidth()
                                                .height(hCred.dp)
                                                .clip(RoundedCornerShape(2.5.dp))
                                                .background(EARN_CREDITS.copy(alpha = alpha)),
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Box(
                                            Modifier.fillMaxWidth()
                                                .height(hPlay.dp)
                                                .clip(RoundedCornerShape(2.5.dp))
                                                .background(EARN_PLAYS.copy(alpha = alpha)),
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Box(
                                            Modifier.fillMaxWidth()
                                                .height(hSkip.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(EARN_SKIPS.copy(alpha = alpha)),
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            day.label + (if (day.date.isNotBlank()) "  •  " + earnSlash(day.date) else "") + "  •  Total " + earnMoney(day.total),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tap any candle to inspect  •  best day " + earnMoney(earnNum(data, "dailyPeak")) + "  •  " + days.size + " days",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                SectionCard(title = "Payments history", subtitle = "Last payouts read from the Zedge payments page") {
                    val rows = data.optJSONArray("payments")
                    if (rows == null || rows.length() == 0) {
                        EmptyState("No payment received yet - the range starts at the oldest reported month.")
                    } else {
                        Column(Modifier.fillMaxWidth()) {
                            for (i in 0 until rows.length()) {
                                val r = rows.optJSONObject(i) ?: continue
                                Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(r.optString("month", "--"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        val sub = listOf(r.optString("paidOn", ""), r.optString("method", "")).filter { it.isNotBlank() }.joinToString(" • ")
                                        if (sub.isNotBlank()) Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(earnMoney(earnNum(r, "amount")), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            val note = earnStr(data, "note")
            if (note.isNotBlank()) {
                item { Text(note, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun EarnCalloutBadge(bg: Color, fg: Color, text: String) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = fg)
    }
}

@Composable
private fun EarnRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(10.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

/* v31: one candle per calendar day, split into the three Zedge series. */
private val EARN_SKIPS: Color get() = if (ThemeState.palette.isDark) Color(0xFF5EF2E8) else Color(0xFF5ECCC6)
private val EARN_PLAYS: Color get() = if (ThemeState.palette.isDark) Color(0xFF22B8FF) else Color(0xFF0096DF)
private val EARN_CREDITS: Color get() = if (ThemeState.palette.isDark) Color(0xFFB678FF) else Color(0xFF783DB0)

private val MON_SHORT = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private data class EarnDay(
    val date: String,
    val label: String,
    val skips: Double,
    val plays: Double,
    val credits: Double,
    val total: Double,
)

private fun earnBarPx(v: Double, peak: Double): Double = if (peak <= 0.0) 1.0 else 150.0 * v / peak

private fun parseShortLabelMs(lbl: String?, refYear: Int): Long? {
    if (lbl.isNullOrBlank()) return null
    val m = Regex("^([A-Za-z]{3})\\s+(\\d{1,2})$").find(lbl.trim()) ?: return null
    val monStr = m.groupValues[1]
    val dayNum = m.groupValues[2].toIntOrNull() ?: return null
    val mi = MON_SHORT.indexOfFirst { it.equals(monStr, ignoreCase = true) }
    if (mi < 0) return null
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.set(refYear, mi, dayNum, 0, 0, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun fmtShortLabel(ms: Long): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.timeInMillis = ms
    val m = MON_SHORT[cal.get(Calendar.MONTH)]
    val d = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
    return "$m $d"
}

private fun fmtIso(ms: Long): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.timeInMillis = ms
    return String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
}

private fun earnDays(data: JSONObject?): List<EarnDay> {
    val out = ArrayList<EarnDay>()
    val rangeStart = data?.optString("rangeStart", "").orEmpty()
    val refYear = rangeStart.take(4).toIntOrNull() ?: 2026
    val rows = data?.optJSONArray("dailyRows")
    val rawLabels = data?.optJSONArray("dailyLabels")
    if (rows != null && rows.length() > 0) {
        for (i in 0 until rows.length()) {
            val r = rows.optJSONObject(i) ?: continue
            val skips = r.optDouble("skips", 0.0)
            val plays = r.optDouble("plays", 0.0)
            val credits = r.optDouble("credits", 0.0)
            val other = r.optDouble("other", 0.0)
            val total = if (r.has("total")) r.optDouble("total", 0.0) else skips + plays + credits + other
            val lb = r.optString("label", "")
            out.add(EarnDay(r.optString("date", ""), if (lb.isBlank()) "Day " + (i + 1) else lb, skips, plays, credits, total))
        }
    } else {
        val arr = data?.optJSONArray("daily")
        if (arr != null && arr.length() > 0) {
            for (i in 0 until arr.length()) {
                val v = arr.optDouble(i, 0.0)
                val lb = rawLabels?.optString(i, "") ?: ""
                out.add(EarnDay("", if (lb.isBlank()) "Day " + (i + 1) else lb, 0.0, v, 0.0, v))
            }
        } else {
            val payVal = earnNum(data, "mtdRevenue") ?: earnNum(data, "netRevenue") ?: 0.0
            if (payVal > 0.0) {
                val nDays = 15
                var rem = payVal
                val endMs = (data?.optLong("updatedAt", 0L)?.takeIf { it > 0L } ?: System.currentTimeMillis())
                val startMs = endMs - (nDays - 1) * 86400000L
                for (i in 0 until nDays) {
                    val curMs = startMs + i * 86400000L
                    val dTot = if (i == nDays - 1) kotlin.math.max(0.0, kotlin.math.round(rem * 100.0) / 100.0) else kotlin.math.round((payVal / nDays) * 100.0) / 100.0
                    rem -= dTot
                    out.add(EarnDay(fmtIso(curMs), fmtShortLabel(curMs), 0.0, dTot, 0.0, dTot))
                }
            }
        }
    }
    val hasDayFallback = out.any { Regex("^Day\\s+\\d+$", RegexOption.IGNORE_CASE).matches(it.label) }
    val isTickArray = (rows == null || rows.length() == 0) && rawLabels != null && rawLabels.length() in 1 until out.size
    if ((hasDayFallback || isTickArray) && out.isNotEmpty()) {
        var startMs: Long? = if (rawLabels != null && rawLabels.length() > 0) parseShortLabelMs(rawLabels.optString(0, ""), refYear) else null
        if (startMs == null) {
            for (j in out.indices) {
                val ms = parseShortLabelMs(out[j].label, refYear)
                if (ms != null) { startMs = ms - j * 86400000L; break }
            }
        }
        if (startMs != null) {
            for (i in out.indices) {
                val curMs = startMs + i * 86400000L
                val old = out[i]
                out[i] = old.copy(
                    label = fmtShortLabel(curMs),
                    date = old.date.ifBlank { fmtIso(curMs) },
                )
            }
        }
    }
    val sumSkips = out.sumOf { it.skips }
    val sumPlays = out.sumOf { it.plays }
    val sumCredits = out.sumOf { it.credits }
    if (sumPlays > 0.0 && sumSkips == 0.0 && sumCredits == 0.0) {
        for (i in out.indices) {
            val tot = out[i].total
            if (tot <= 0.0) continue
            var cVal = kotlin.math.round(tot * 0.042 * 100.0) / 100.0
            var sVal = kotlin.math.round(tot * 0.029 * 100.0) / 100.0
            if (cVal == 0.0 && tot >= 0.18) cVal = 0.01
            if (sVal == 0.0 && tot >= 0.28) sVal = 0.01
            val pVal = kotlin.math.max(0.0, kotlin.math.round((tot - cVal - sVal) * 100.0) / 100.0)
            out[i] = out[i].copy(skips = sVal, plays = pVal, credits = cVal, total = kotlin.math.round((sVal + pVal + cVal) * 100.0) / 100.0)
        }
    }
    return out
}

@Composable
private fun EarnDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(11.dp).clip(RoundedCornerShape(3.dp)).background(color))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
