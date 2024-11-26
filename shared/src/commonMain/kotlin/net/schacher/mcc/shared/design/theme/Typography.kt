package net.schacher.mcc.shared.design.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MccTypography = Typography().let {
    it.copy(
        h1 = it.h1.copy(fontWeight = FontWeight.SemiBold),
        h2 = it.h2.copy(fontWeight = FontWeight.SemiBold),
        h3 = it.h3.copy(fontWeight = FontWeight.SemiBold),
        h4 = it.h4.copy(fontWeight = FontWeight.SemiBold),
        h5 = it.h5.copy(fontWeight = FontWeight.SemiBold),
        h6 = it.h6.copy(fontWeight = FontWeight.SemiBold),
        caption = it.caption.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        ),
    )
}