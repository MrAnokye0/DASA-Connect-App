package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EventViewModel

@Composable
fun authGoldShimmerBrush(): Brush {
    val shimmerColors = listOf(
        DasaGold,
        Color(0xFFFFEEAB),
        DasaGold,
        Color(0xFFAC881C),
        DasaGold
    )
    val transition = rememberInfiniteTransition(label = "AuthButtonShimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Translate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 400f, translateAnim + 400f)
    )
}

@Composable
fun PremiumTextFieldWrapper(
    isFocused: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1.0f else 0f,
        animationSpec = tween(250, easing = EaseInOutCubic),
        label = "FocusGlowAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.01f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "FocusScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                if (glowAlpha > 0f) {
                    // Draw a soft gold glowing double outline
                    drawRoundRect(
                        color = DasaGold.copy(alpha = glowAlpha * 0.12f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                        style = Stroke(width = 6.dp.toPx())
                    )
                }
            }
            .shadow(
                elevation = if (isFocused) 6.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = DasaGold.copy(alpha = 0.12f),
                spotColor = DasaGold.copy(alpha = 0.22f)
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Delegate") }
    
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }

    val roles = listOf("Delegate", "Speaker", "Sponsor", "Volunteer")

    // Welcome sequential stagger trigger
    val animTrigger = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animTrigger.value = true
    }

    val logoAlpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(900, delayMillis = 100, easing = EaseOutCubic),
        label = "LogoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0.75f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "LogoScale"
    )

    val welcomeAlpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(900, delayMillis = 300, easing = EaseOutCubic),
        label = "WelcomeAlpha"
    )
    val welcomeSlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 30f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "WelcomeSlide"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 500, easing = EaseOutCubic),
        label = "CardAlpha"
    )
    val cardSlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 40f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "CardSlide"
    )

    // Staggered sequential reveal metrics for internal card elements
    val f1Alpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(600, delayMillis = 650, easing = EaseOutQuad),
        label = "F1Alpha"
    )
    val f1SlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "F1Slide"
    )

    val f2Alpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(600, delayMillis = 750, easing = EaseOutQuad),
        label = "F2Alpha"
    )
    val f2SlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "F2Slide"
    )

    val f3Alpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(600, delayMillis = 850, easing = EaseOutQuad),
        label = "F3Alpha"
    )
    val f3SlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "F3Slide"
    )

    val f4Alpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(600, delayMillis = 950, easing = EaseOutQuad),
        label = "F4Alpha"
    )
    val f4SlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "F4Slide"
    )

    val f5Alpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(600, delayMillis = 1050, easing = EaseOutQuad),
        label = "F5Alpha"
    )
    val f5SlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "F5Slide"
    )

    val f6Alpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(600, delayMillis = 1150, easing = EaseOutQuad),
        label = "F6Alpha"
    )
    val f6SlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 15f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "F6Slide"
    )

    val fSubmitAlpha by animateFloatAsState(
        targetValue = if (animTrigger.value) 1f else 0f,
        animationSpec = tween(700, delayMillis = 1250, easing = EaseOutQuad),
        label = "SubmitAlpha"
    )
    val fSubmitSlideY by animateFloatAsState(
        targetValue = if (animTrigger.value) 0f else 20f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "SubmitSlide"
    )

    // Focus state trackers
    var nameFocused by remember { mutableStateOf(false) }
    var companyFocused by remember { mutableStateOf(false) }
    var titleFocused by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DasaDeepNavy, LuxuryDarkBg)
                )
            )
            .systemBarsPadding()
    ) {
        // Subtle ambient light spots (glowing fintech orbs)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DasaGold.copy(alpha = 0.04f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = Offset(0f, 0f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(DasaEmerald.copy(alpha = 0.03f), Color.Transparent),
                    center = Offset(size.width, size.height * 0.4f),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = Offset(size.width, size.height * 0.4f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // DASA Branded Icon / Decorative Header
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .alpha(logoAlpha)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    }
                    .background(DasaGold.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .border(1.5.dp, DasaGold, RoundedCornerShape(24.dp))
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = DasaGold.copy(alpha = 0.15f),
                        spotColor = DasaGold.copy(alpha = 0.25f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Token,
                    contentDescription = "DASA Connect Logo Icon",
                    tint = DasaGold,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .alpha(welcomeAlpha)
                    .graphicsLayer { translationY = welcomeSlideY },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DASA CONNECT",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DasaGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Digital Assets Summit Africa 2026",
                    style = MaterialTheme.typography.titleMedium,
                    color = LuxuryTextMuted,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Glassmorphism Container Card with elegant shadow and high-contrast glowing border
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha)
                    .graphicsLayer { translationY = cardSlideY }
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = DasaGold.copy(alpha = 0.10f),
                        spotColor = DasaGold.copy(alpha = 0.20f)
                    )
                    .testTag("auth_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryCardBg.copy(alpha = 0.70f)),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.16f),
                            DasaGold.copy(alpha = 0.32f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tabs for Switching login / register
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxuryDarkBg, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { isRegisterMode = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_register"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRegisterMode) DasaGold else Color.Transparent,
                                contentColor = if (isRegisterMode) DasaDeepNavy else LuxuryTextMuted
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text("Register", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { isRegisterMode = false },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_login"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isRegisterMode) DasaGold else Color.Transparent,
                                contentColor = if (!isRegisterMode) DasaDeepNavy else LuxuryTextMuted
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text("Log In", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic Fields based on Mode wrapped in beautiful transition and focus layout
                    AnimatedContent(
                        targetState = isRegisterMode,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(350, easing = EaseOutQuad)) togetherWith
                            fadeOut(animationSpec = tween(300, easing = EaseInQuad))
                        },
                        label = "AuthFieldsTransition"
                    ) { targetMode ->
                        if (targetMode) {
                            // Register Mode Fields
                            Column {
                                // Full Name
                                PremiumTextFieldWrapper(
                                    isFocused = nameFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f1Alpha)
                                        .graphicsLayer { translationY = f1SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = { Text("Full Name") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DasaGold) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { nameFocused = it.isFocused }
                                            .testTag("input_name")
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Company
                                PremiumTextFieldWrapper(
                                    isFocused = companyFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f2Alpha)
                                        .graphicsLayer { translationY = f2SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = company,
                                        onValueChange = { company = it },
                                        label = { Text("Company / Organization") },
                                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = DasaGold) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { companyFocused = it.isFocused }
                                            .testTag("input_company")
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Job Title
                                PremiumTextFieldWrapper(
                                    isFocused = titleFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f3Alpha)
                                        .graphicsLayer { translationY = f3SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = title,
                                        onValueChange = { title = it },
                                        label = { Text("Job Title") },
                                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = DasaGold) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { titleFocused = it.isFocused }
                                            .testTag("input_title")
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Role Selector
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f4Alpha)
                                        .graphicsLayer { translationY = f4SlideY }
                                ) {
                                    Text(
                                        text = "Select Event Role:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LuxuryTextMuted,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        textAlign = TextAlign.Start
                                    )
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        roles.forEach { role ->
                                            val isSelected = selectedRole == role
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { selectedRole = role },
                                                label = { Text(role) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = DasaGold,
                                                    selectedLabelColor = DasaDeepNavy,
                                                    containerColor = LuxuryDarkBg,
                                                    labelColor = LuxuryTextMuted
                                                ),
                                                border = FilterChipDefaults.filterChipBorder(
                                                    enabled = true,
                                                    selected = isSelected,
                                                    selectedBorderColor = DasaGold,
                                                    borderColor = BorderColor
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Email
                                PremiumTextFieldWrapper(
                                    isFocused = emailFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f5Alpha)
                                        .graphicsLayer { translationY = f5SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("Professional Email") },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DasaGold) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { emailFocused = it.isFocused }
                                            .testTag("input_email")
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Password
                                PremiumTextFieldWrapper(
                                    isFocused = passwordFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f6Alpha)
                                        .graphicsLayer { translationY = f6SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DasaGold) },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                                Icon(
                                                    imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle password visibility",
                                                    tint = DasaGold
                                                )
                                            }
                                        },
                                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { passwordFocused = it.isFocused }
                                            .testTag("input_password")
                                    )
                                }
                            }
                        } else {
                            // Login Mode Fields (f1 and f2 staggered)
                            Column {
                                // Email
                                PremiumTextFieldWrapper(
                                    isFocused = emailFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f1Alpha)
                                        .graphicsLayer { translationY = f1SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("Professional Email") },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DasaGold) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { emailFocused = it.isFocused }
                                            .testTag("input_email")
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Password
                                PremiumTextFieldWrapper(
                                    isFocused = passwordFocused,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(f2Alpha)
                                        .graphicsLayer { translationY = f2SlideY }
                                ) {
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DasaGold) },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                                Icon(
                                                    imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle password visibility",
                                                    tint = DasaGold
                                                )
                                            }
                                        },
                                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DasaGold,
                                            unfocusedBorderColor = BorderColor,
                                            focusedLabelColor = DasaGold,
                                            unfocusedLabelColor = LuxuryTextMuted,
                                            focusedTextColor = LuxuryTextLight,
                                            unfocusedTextColor = LuxuryTextLight,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { passwordFocused = it.isFocused }
                                            .testTag("input_password")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Shimmering button setup with click scales
                    val btnInteractionSource = remember { MutableInteractionSource() }
                    val btnPressed by btnInteractionSource.collectIsPressedAsState()
                    val btnScale by animateFloatAsState(
                        targetValue = if (btnPressed) 0.96f else 1.0f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "ButtonPressScale"
                    )
                    val activeShimmerBrush = authGoldShimmerBrush()

                    // Main Action Button (staggered delay)
                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                if (name.isNotBlank() && email.isNotBlank()) {
                                    viewModel.registerUser(
                                        name = name,
                                        email = email,
                                        company = company.ifBlank { "Independent" },
                                        title = title.ifBlank { "Delegate" },
                                        role = selectedRole
                                    )
                                }
                            } else {
                                if (email.isNotBlank()) {
                                    viewModel.registerUser(
                                        name = "Executive Delegate",
                                        email = email,
                                        company = "Apex Institutional",
                                        title = "Investment Director",
                                        role = "Delegate"
                                    )
                                }
                            }
                        },
                        interactionSource = btnInteractionSource,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = DasaDeepNavy
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .alpha(fSubmitAlpha)
                            .graphicsLayer {
                                scaleX = btnScale
                                scaleY = btnScale
                                translationY = fSubmitSlideY
                            }
                            .background(activeShimmerBrush, RoundedCornerShape(12.dp))
                            .shadow(
                                elevation = if (btnPressed) 3.dp else 8.dp,
                                shape = RoundedCornerShape(12.dp),
                                ambientColor = DasaGold.copy(alpha = 0.20f),
                                spotColor = DasaGold.copy(alpha = 0.40f)
                            )
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isRegisterMode) "Create Secured Badge" else "Secure Signature Sign-In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isRegisterMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            modifier = Modifier
                                .alpha(fSubmitAlpha)
                                .graphicsLayer { translationY = fSubmitSlideY }
                                .testTag("forgot_password_button")
                        ) {
                            Text(
                                "Forgot Passcode Signature?",
                                color = DasaGold,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer Brand Trust info
            Text(
                text = "Secure end-to-end JWT asymmetric verified connection.\nDASA Cyber-operations framework compliance.",
                style = MaterialTheme.typography.labelSmall,
                color = LuxuryTextMuted.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.alpha(welcomeAlpha)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Secure Signature Key", color = DasaGold) },
            text = {
                Column {
                    Text("Enter your registered executive email to receive an authorization link.", color = LuxuryTextLight, modifier = Modifier.padding(bottom = 16.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Secure Email Address") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DasaGold,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = LuxuryTextLight,
                            unfocusedTextColor = LuxuryTextLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DasaGold, contentColor = DasaDeepNavy)
                ) {
                    Text("Send Recovery Signature")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = DasaGold)
                }
            },
            containerColor = LuxuryCardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
