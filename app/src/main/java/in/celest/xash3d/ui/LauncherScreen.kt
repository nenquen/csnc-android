package `in`.celest.xash3d.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.ui.text.style.TextOverflow
import `in`.celest.xash3d.csbtem.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen(
    cmdArgs: String,
    onCmdArgsChange: (String) -> Unit,
    useVolume: Boolean,
    onUseVolumeChange: (Boolean) -> Unit,
    pixelFormat: Int,
    onPixelFormatChange: (Int) -> Unit,
    resizeWorkaround: Boolean,
    onResizeWorkaroundChange: (Boolean) -> Unit,
    immersiveMode: Boolean,
    onImmersiveModeChange: (Boolean) -> Unit,
    resolution: Boolean,
    onResolutionChange: (Boolean) -> Unit,
    isCustomResolution: Boolean,
    onIsCustomResolutionChange: (Boolean) -> Unit,
    resWidth: String,
    onResWidthChange: (String) -> Unit,
    resHeight: String,
    onResHeightChange: (String) -> Unit,
    resScale: String,
    onResScaleChange: (String) -> Unit,
    resPath: String,
    onPathClick: () -> Unit,
    onLaunchClick: () -> Unit
) {
    val tabs = listOf(stringResource(R.string.text_tab1), stringResource(R.string.text_tab2))
    
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            AboutContent()
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("CSNC") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 640.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SlidingPillNavigation(
                            tabs = tabs,
                            pagerState = pagerState,
                            onTabSelected = { index ->
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        )
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            if (page == 0) {
                                NormalSettings(
                                    resPath = resPath,
                                    onPathClick = onPathClick,
                                    onLaunchClick = onLaunchClick,
                                    onAboutClick = { showBottomSheet = true }
                                )
                            } else {
                                AdvancedSettings(
                                    cmdArgs, onCmdArgsChange,
                                    useVolume, onUseVolumeChange,
                                    pixelFormat, onPixelFormatChange,
                                    resizeWorkaround, onResizeWorkaroundChange,
                                    immersiveMode, onImmersiveModeChange,
                                    resolution, onResolutionChange,
                                    isCustomResolution, onIsCustomResolutionChange,
                                    resWidth, onResWidthChange,
                                    resHeight, onResHeightChange,
                                    resScale, onResScaleChange
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NormalSettings(
    resPath: String,
    onPathClick: () -> Unit,
    onLaunchClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Column {

        ElevatedCard(
            modifier = Modifier.fillMaxWidth().clickable { onPathClick() },
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.csnc),
                        contentDescription = "CSNC Logo",
                        modifier = Modifier.size(80.dp).offset(y = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.text_res_path),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = resPath,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLaunchClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(stringResource(R.string.launch_button), style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onAboutClick,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(stringResource(R.string.about_button), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun AdvancedSettings(
    cmdArgs: String, onCmdArgsChange: (String) -> Unit,
    useVolume: Boolean, onUseVolumeChange: (Boolean) -> Unit,
    pixelFormat: Int, onPixelFormatChange: (Int) -> Unit,
    resizeWorkaround: Boolean, onResizeWorkaroundChange: (Boolean) -> Unit,
    immersiveMode: Boolean, onImmersiveModeChange: (Boolean) -> Unit,
    resolution: Boolean, onResolutionChange: (Boolean) -> Unit,
    isCustomResolution: Boolean, onIsCustomResolutionChange: (Boolean) -> Unit,
    resWidth: String, onResWidthChange: (String) -> Unit,
    resHeight: String, onResHeightChange: (String) -> Unit,
    resScale: String, onResScaleChange: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Command Line",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = cmdArgs,
                onValueChange = onCmdArgsChange,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            ListItem(
                headlineContent = { Text(stringResource(R.string.volume)) },
                trailingContent = { Switch(checked = useVolume, onCheckedChange = onUseVolumeChange) },
                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.resize_on)) },
                trailingContent = { Switch(checked = resizeWorkaround, onCheckedChange = onResizeWorkaroundChange) },
                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )

            ListItem(
                headlineContent = { Text("Enable immersive mode") },
                trailingContent = { Switch(checked = immersiveMode, onCheckedChange = onImmersiveModeChange) },
                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )

            ListItem(
                headlineContent = { Text("Fixed screen resolution") },
                trailingContent = { Switch(checked = resolution, onCheckedChange = onResolutionChange) },
                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )

            if (resolution) {
                // Set custom resolution to true when resolution is enabled to match the simplified UI
                LaunchedEffect(Unit) {
                    onIsCustomResolutionChange(true)
                }

                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = resWidth,
                                onValueChange = onResWidthChange,
                                label = { Text("Width", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                )
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            OutlinedTextField(
                                value = resHeight,
                                onValueChange = onResHeightChange,
                                label = { Text("Height", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidingPillNavigation(
    tabs: List<String>,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        val maxWidth = maxWidth
        val tabWidth = maxWidth / tabs.size
        
        // Real-time position tracking
        val pagerOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
        val indicatorOffset = tabWidth * pagerOffset

        // Sliding Background Pill
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        )

        // Tab Labels
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                         color = if (pagerState.currentPage == index) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AboutContent() {
    val uriHandler = LocalUriHandler.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Physic3D",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "nenquen.github.io",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { uriHandler.openUri("https://nenquen.github.io") }
                )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AboutLinkButton("GitHub", "https://github.com/physic3d/")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AboutSectionHeader("Physic3D Team")
        Text(
            text = "nenquen, iQuitt, bariscodefxy, hasandramali",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AboutSectionHeader("Special Thanks")
        Text(
            text = "Xash3D FWGS Team, CSMoE",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AboutSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 4.dp),
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun AboutLinkButton(text: String, url: String) {
    val uriHandler = LocalUriHandler.current
    FilledTonalButton(
        onClick = { uriHandler.openUri(url) },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}
