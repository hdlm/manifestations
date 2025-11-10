package com.budoxr.Exercises.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoSim
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budoxr.manifestations.BuildConfig
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onBooleanType
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.commons.util.PreviewHelper
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessons
import com.budoxr.manifestations.data.mapper.emptyJournalEntity
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.di.Modules.unitTestModule
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.presenters.JournalScreenUiState
import com.budoxr.manifestations.presentation.presenters.JournalViewModel
import com.budoxr.manifestations.presentation.presenters.KoinViewModel
import com.budoxr.manifestations.ui.components.ComboBox
import com.budoxr.manifestations.ui.components.HorizontalDraggableJournalItemList
import com.budoxr.manifestations.ui.components.InfoDialog
import com.budoxr.manifestations.ui.components.JournalForm
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.grayLight
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import java.util.Date


data class JournalState(
    val manifestationId: Int,
    val manifestationSlug: String?,
    val manifestationMenuItems: Array<String>,
    val lessonDayItems: Array<String>,
    val manifestations: List<ManifestationModel>,
    val lessons: LessonsWrapper,
    val categoryColor: (String, Context) -> Color,
    val onItemDeleteClick: (JournalEntity) -> Unit,
    val dateDifference: (String, String) -> Long,
    val onFetchManifestationWithLessons: (Int) -> Unit,
    val onFetchJournal: (Int) -> Unit,
    val onSaveLesson: (LessonEntity, onIntType) -> Unit,
    val onSaveButtonClick: (JournalEntity, Int) -> Unit,
    /** using the hash code */
    val onLongPress: onIntType,
)


@Composable
fun JournalScreen(
    navController: NavController,
    page: Int,
    manifestationId: Int,
    lessonDay: Int,
    onEditMode: onIntType,
    viewModel: JournalViewModel = koinViewModel()

) {
    Log.i(TAG, "compose / recompose")

    val lessons by viewModel.manifestationWithLessons.collectAsStateWithLifecycle()
    val journals by viewModel.journals.collectAsStateWithLifecycle()
    viewModel.collectJournals(manifestationId)

    val journalScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val uiState = journalScreenUiState) {
        is JournalScreenUiState.Loading -> {
            LocalPref.saveSession(
                viewModel.session.apply {
                    currentScreen = Screens.JournalScreen.route
                    manifestation = manifestationId
                    lesson = lessonDay
                }
            )
            JournalScreenLoading()
        }
        is JournalScreenUiState.Error -> {
            JournalScreenError(
                msg = uiState.errorMessage!!,
                onRetry = {
                    viewModel.errorShowed = true
                    viewModel.refresh(force = true)
                }
            )
        }
        is JournalScreenUiState.Ready -> {
            JournalScreenReady(
                page = page,
                manifestationId = manifestationId,
                lessons = lessons,
                journals = journals,
                navController = navController,
                uiState = uiState,
                onEditMode = onEditMode,
                viewModel = viewModel
            )
        }
    }

}

@Composable
fun JournalScreenLoading(modifier: Modifier = Modifier,
) {

    val iconSize = dimensionResource(id = R.dimen.icon_huge_size)
    val areaSize = 94.dp

    Box {
        CircularProgressIndicator(
            modifier = Modifier
                .size(areaSize)
                .align(Alignment.Center),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Image( modifier = Modifier
            .align(Alignment.Center)
            .clip(CircleShape)
            .size(iconSize),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.content_description_logo),
            contentScale = ContentScale.Fit,
        )
    }
}


@Composable
fun JournalScreenError(msg: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(id = R.string.msg_an_error_has_ocurred),
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text =  msg,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onRetry) {
            Text(text = stringResource(id = R.string.label_retry))
        }
    }

}

@Composable
fun JournalScreenReady(
    page: Int,
    manifestationId: Int,
    lessons: List<ManifestationWithLessons>,
    journals: List<LessonWithJournals>,
    navController: NavController,
    uiState: JournalScreenUiState.Ready,
    onEditMode: onIntType,
    viewModel: JournalViewModel) {

    val horizontalMargin = dimensionResource(id = R.dimen.margin_horizontal)

    val coroutineScope = rememberCoroutineScope()
    var searchPattern by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(emptyJournalEntity()) }
    var page by remember { mutableStateOf(page) }
    var showDialogForDelete by remember { mutableStateOf(false) }
    var showDialogForManifestationEmpty by remember { mutableStateOf(false) }

    val onLongPress: onIntType = { hashCode ->
        Log.d(TAG, "onLongPress() -> invoked, id: $hashCode")
        onEditMode.invoke(hashCode)
    }
    val onSaveLesson: (LessonEntity, onIntType) -> Unit = { lesson, onDone ->
        Log.d(TAG, "onSaveLesson() -> invoked")
        viewModel.saveLesson(lesson, onDone)

        val firstPop = navController.popBackStack()
        Log.d(TAG, "onSaveButtonClick() -> clicked\n\treturned first pop: $firstPop")
    }
    val onSaveButtonClick: (JournalEntity, lessonId: Int) -> Unit = { journal, lessonId ->
        viewModel.saveJournal( journal, lessonId )
    }
    val onItemDeleteClick: (JournalEntity) -> Unit = { journal ->
        Log.d(TAG, "onItemDeleteClick() -> invoked, journal id: ${journal}")
        selectedItem = journal
        showDialogForDelete = true
    }
    val onButtonConfirmationDelete: onBooleanType = { confirm ->
        Log.d(TAG, "onButtonJournalConfirmationDelete() -> invoked: $confirm, id: ${selectedItem} ")
        if (confirm) {
            viewModel.deleteJournal(selectedItem)
        }
        showDialogForDelete = false
        selectedItem = emptyJournalEntity()
    }
    val onButtonJournalManifestationEmpty: onDismissType = {
        Log.d(TAG, "onButtonJournalManifestationEmpty() -> invoked")
        showDialogForManifestationEmpty = false
        navController.popBackStack()
    }
    val journalState = JournalState(
        manifestationId = manifestationId,
        manifestationSlug = uiState.manifestations.find { it.id == manifestationId }?.overview,
        manifestationMenuItems =  viewModel.util.transformList(uiState.manifestations) { it.overview }.toTypedArray(),
        lessonDayItems = viewModel.util.transformList(uiState.lessons.lessons) { it.day.toString() }.toTypedArray(),
        manifestations = uiState.manifestations,
        lessons = uiState.lessons,
        categoryColor = viewModel::categoryColor,
        onItemDeleteClick = onItemDeleteClick,
        dateDifference = viewModel::dateDifference,
        onFetchManifestationWithLessons = viewModel::collectManifestationWithLessons,
        onFetchJournal = viewModel::collectJournals,
        onSaveLesson = onSaveLesson,
        onSaveButtonClick = onSaveButtonClick,
        onLongPress = onLongPress,
    )

    Column (modifier = Modifier
        .fillMaxSize()
    ) {
        when (page) {
            0 -> {
                JournalScreenBody(
                    journalState = journalState,
                    journals = journals.distinctBy { it.lesson.id }
                )
            }
            1 -> { // add new Journal
                if (journalState.manifestationMenuItems.isNotEmpty()) {
                    JournalForm(
                        manifestationSlug = journalState.manifestationSlug!!,
                        manifestationMenuItems = journalState.manifestationMenuItems,
                        lessonDayItems = journalState.lessonDayItems,
                        manifestations = journalState.manifestations,
                        lessons = journalState.lessons,
                        onSaveLesson = journalState.onSaveLesson,
                        onSaveButtonClick = journalState.onSaveButtonClick,
                        modifier = Modifier.padding(horizontal = horizontalMargin),
                    )
                } else {
                    showDialogForManifestationEmpty = true
                }

            }
        }

        if ( showDialogForManifestationEmpty ) {
            InfoDialog(
                modifier = Modifier,
                title = stringResource(id = R.string.title_manifestation_empty),
                msg = stringResource(id = R.string.msg_manifestation_empty),
                buttonLabel = stringResource(id = R.string.button_validation_confirm ),
                onDone =  onButtonJournalManifestationEmpty
            )
        }
    }
    
    
}

@Composable
fun JournalScreenBody(
    journalState: JournalState,
    journals: List<LessonWithJournals>
) {
    val iconSize = dimensionResource(id = R.dimen.icon_big_size)
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)

    val manifestation = remember { mutableStateOf(TextFieldValue(journalState.manifestationMenuItems.find { it == journalState.manifestationSlug }!! )) }
    val day = remember { mutableStateOf(TextFieldValue(journalState.lessonDayItems.first())) }

    if (BuildConfig.CAPTURE_JSON) {
        val json = PreviewHelper.journalStateToJSON(journalState)
    }

    LazyColumn(modifier = Modifier.padding(horizontal = marginHorizontal)) {
        item {
            ComboBox(
                enabled = false,
                items = journalState.manifestationMenuItems,
                label = stringResource(R.string.label_manifestation),
                field = manifestation,
                onSelectedItem = { _ ->},
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
//            ComboBox(
//                items = journalState.lessonDayItems,
//                label = stringResource(R.string.label_lesson),
//                field = day,
//                omitLabel = false,
//                modifier = Modifier
//            )
        }

        if (journals.isNotEmpty()) {
            val items = journals.first()._journals
            val manifestationId = journals.first().lesson.manifestationId
            val manifestation =  journalState.manifestations.find { it.id == manifestationId }!!
            //TODO el listado cambio, se deberia mostrar una tarjeta por cada leccion realizada de un dia en especifico, y quizas mostrar las preguntas y respuestas desplegables al hacer click a un button de vista
            items(items) { item ->
                HorizontalDraggableJournalItemList(
                    manifestation = manifestation,
                    item = item,
                    lessons = journalState.lessons.lessons,
                    days = journalState.dateDifference.invoke(
                        Date().toFechaTimeDb(),
                        manifestation.dueDate
                    ),
                    categoryColor = journalState.categoryColor,
                    onItemDeleteClick = journalState.onItemDeleteClick,
                    onLongPress = journalState.onLongPress,
                )
                Spacer(modifier = Modifier.padding(vertical = lineSpacing))

            }
        } else {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.label_no_records),
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.padding(vertical = lineSpacing))
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(iconSize),
                            imageVector = Icons.Outlined.NoSim,
                            contentDescription = stringResource(id = R.string.content_description_icon)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            }
        }
    }

    
}


@Composable
@Preview(showBackground = true)
fun JournalScreenPreview() {

    KoinApplication( application =  {
        modules(unitTestModule)
    }) {
        val manifestationsJson = """
        [{"id":1,"overview":"First","description":"First description","creation_date":"2025-02-06 04:13:57","due_date":"2025-02-28 20:00:00","category":"Contribution"},{"id":2,"overview":"Second","description":"Second description","creation_date":"2025-02-11 20:00:00","due_date":"2025-03-12 20:00:00","category":"Purpose"},{"id":3,"overview":"","description":"","creation_date":"2025-02-06 13:11:56","due_date":"2025-02-06 13:11:56","category":""}]
    """.trimIndent()
        val lessonsJson = """
        {"lessons":[{"day":1,"subject":"The 80/20 Trap","summary":["To become a masterful manifestor, focus 80% on your mindset and 20% on your actions","What you focus on expands. Therefore, it's essential to get clear and precise on your focus point","The 9 areas of your life are: Health, Wealth, Relationships, Mindset, Emotions, Purpose, Passion, Contribution, Spirituality"],"journal":["What are you committed to during this Quest?","What is going to be your focus point?","How will you commit yourself to integrating the80% into your life? Write three specific commitments"]},{"day":2,"subject":"The Power of 2%: Take Control of Your Inner Reality","summary":["When you rewire your belief system, your manifestations start spiraling up","The power of 2% is the compounding effect on your internal reality, resulting in compounding manifestations."],"journal":["Ask yourself: Have you been mostly spiraling up or down?","Who do you now get to be? Are you someone who spirals up with your manifestations or someone who spirals down?","What is my 2% commitment to my belief system anddaily action? And make sure this is tuned to the area of your life on which you're focusing"]},{"day":3,"subject":"Tap Into Surrendered Manifestation","summary":["Manifestation is taking your intention and allowing it to be embodied and anchored through you","Surrendered manifestation is when you're not trying to control every little detail of the process","The key is surrendering the how while still showing up and taking aligned action"],"journal":["What are your biggest breakthroughs? Share it withYour Tribe","Create a list of how you have attempted to control the manifestation process in the past","Ask yourself, what are you committed to now?"]},{"day":4,"subject":"Allow Yourself to Dream Again","summary":["If you don't claim what you truly desire, you will never manifest it","Alignment means aligning your vision with your soul's pathway","When writing your intention, be clear and specific. Anchor it, embody it and allow it to be already solidified in the now"],"journal":["What would it be, if you could wave a magic wand and allow yourself to manifest anything?","What would you ask if you were unapologetic with what you truly desire?","Create two specific intentions. One for manifestation on or before this Quest, one for a more extended period of time","Share your two specific manifestations with the Tribe"],"meditation":"01_activate_your_true_vision_es.txt"},{"day":5,"subject":"Your Total Life Analysis","summary":["When you have a holistic approach to manifesting, you upgrade every area of your life","When you realize you're at a minimum standard, you raise your level of tolerance","Have deep gratitude for where you are, but accept nothing less than the new minimum standard"],"journal":["Take a look at every single category of your life and rate them out of 10.","Raise your level of tolerance by writing down yournew minimum standard for each area of your life. Share it with the Tribe","In your focus area, how will you now choose to be? Share it with the Tribe"],"meditation":"02_anchor_in_a_new_version_of_yourself_es.txt"},{"day":6,"subject":"Get Clear On Your Big Picture Vision","summary":["Every single layer of your being must be in alignment with your vision","To accelerate your manifestation, you must anchor it into your reality","Make sure you're clear on your intention, when it will manifest, and how you will measure it"],"journal":["Ask yourself: what do you notice is different?","What are you committed to from here on out?","Share it with the Tribe"],"meditation":"03_anchor_in_your_vision_es.txt"},{"day":7,"subject":"Build Awareness of Your Energetic Containers","summary":[" To manifest what you desire, you must have energetic containers for your manifestations","Energetic containers are intentional spaces you create within your consciousness","You create space in your energetic field when you understand why you want to manifest"],"journal":["What happened through this lesson? What were theaha moments? What did you realize and what do you now notice that is different?","Share your breakthroughs with the Tribe."],"meditation":"04_build_awareness_of_your_energ_es.txt"},{"day":8,"subject":"Expand Beyond Your Limits","summary":[" Your unconscious mind is in control of your reality. It must be aligned with where you want to be","Your unconscious mind creates upper limits to keep you safe and in your comfort zone.","To shift past your upper limits, focus on expanding your energetic containers"],"journal":["Write down what your vision would be if your big picture manifestation was already done","Create a list of a minimum of 20 reasons why your vision beyond your vision is not only safe but amazing and incredible","Share it with the Tribe"]},{"day":9,"subject":"Anchor in Your Vision Beyond Your Vision","summary":["When your unconscious mind knows that your vision is safe, you can create massive shifts in your manifestations","Go through the activation twice","Make sure you are connected to your vision beyond your vision and have anchored in the two dates"],"journal":["After you’ve listened to the activation reflecton what is different. What do you notice?","Share your breakthrough with the Tribe","Note: Go through the activation a second time, focusing on the vision beyond your vision for your long-term manifestation"],"meditation":"05_anchor_in_your_vision_beyond_es.txt"},{"day":10,"subject":"Rewire Your Belief System","summary":["Your belief system is like software currently running within your mind","Your unconscious mind creates a program for your unconscious reality to run on. You must update your program"," When you keep on recalibrating your belief system, your new beliefs become your now beliefs"],"journal":[" If you knew with 100% certainty that your manifestation for this Quest was done, what would you choose to believe? Write down a minimum of 30 new empowering beliefs","Go into the Sleep Meditation","Notice what is different when you wake up. Share it with the Tribe"]},{"day":11,"subject":"Get Raw & Real with Your Limits","summary":["If you’re not in a state of manifestation, look at what is blocking you or slowing you down"," Different types of limitations: Fears, doubts, and worries, Overthinking, Stuck emotions, Self-sabotage patterns, limiting beliefs, and disempowering stories, being too far out into the future or living in the past","The goal is not about having no limits but moving forward and taking action despite your limits"],"journal":["What is stopping you from having your intentions and manifestations show up in your life?","What are you now committed to taking action on,regardless of these limits?","Take action on three commitments today","Share your commitments with the Tribe"],"meditation":"07_alchemize_your_limits_es.txt"},{"day":12,"subject":"The Passionate Fear Override","summary":[" Fear is a natural human response, but it can stop you from manifesting if it shows up uncontrollably","To re-code fear, you must release it from the root cause, the first experience when your system generated a fear response"],"journal":["How does fear show up for you? What would it be if you were to have a different, more powerful experience when fear came up?","What came up for you during your activation?","Share it with the Tribe"],"meditation":"08_release_the_root_cause_of_fear_es.txt"},{"day":13,"subject":"How to Blast Through Self Sabotage","summary":["The self-sabotage patterns are your last attempt to keep you safe and in your comfort zone","You can accelerate your manifestation if you master catching your self-sabotage patterns","You raise your minimum standard when you choose not to tolerate self-sabotage anymore"],"journal":["Create a list of how you have self-sabotaged in the past","Write down what are all the ways you could potentially mess up your manifestation for this Quest?","What are you committed to when it comes to your old self-sabotage patterns?"]},{"day":14,"subject":"Re-Code Your Self Belief","summary":["You can't write the new programming system if you're stuck with the old software","When re-coding your belief system, allow yourself to tune into the frequency of believing it"],"journal":["What do you currently believe when it comes to manifesting X, Y, or Z? Do this for both of your manifestations, one for your Quest and the second for your long-term manifestation","For each of the limiting self-beliefs, write down a minimum of three empowering self-beliefs to start overriding the old program.","Share it with the Tribe"],"meditation":"09_activate_your_belief_system_es.txt"},{"day":15,"subject":"Quieten Your “How” Addiction","summary":["When you're addicted to the how,you're in a frequency of doubt and fear, which can block you from manifesting.","Overthinking is a form of control. You must release control to activate surrendered manifestation"],"journal":["Do two how dump sand clear your unconscious mind from spinning around in the how. One for your Quest and for your larger manifestation","Notice what is different within you and share it with the Tribe"]},{"day":16,"subject":"Remove Resistance & Transform It Into Gifts","summary":["Every layer of your being, including your physical body, must be in alignment with what you want to manifest","In order to manifest faster, you must become an energetic match for what you’re calling in","You must remove trauma from your body to return to your pure natural crystalline state"],"journal":["Ask yourself, what is currently holding you back from your manifestations?","For each of these limits that are still present for you, where do each of these show up within your body?"," Note:Do this Soul Work and listen to the activation for both your Quest and your long-ter manifestation. Pick only one part of the body at atime to work with the clearing process."],"meditation":"10_clear_blocks_from_your_body_es.txt"},{"day":17,"subject":"Re-code New Structures of Reality Into Your Field","summary":["You have a current blueprint running in your energetic field, which alters your unconscious mind and belief system","You must update your blueprint so you can come into greater alignment with your manifestation"],"journal":["Write down what you notice is different within you at a subtle layer","Share your breakthrough with the Tribe","Note: Do the activation twice, once for your specific manifestation on or before the end of the Quest. Then repeat the process and do the activation for your long-term manifestation."],"meditation":"11_recode_new_structures_of_reality_es.txt"},{"day":18,"subject":"The Key to Becoming an Energetic Match","summary":["You must energetically resonate with the frequency of already having what you want to manifest","Becoming an energetic match is about returning to your natural state of abundance","Your frequency is your energetic exchange with the universe. It is your currency for your manifestations"],"journal":["Set your Manifestation Alarm, ideally every hour, every day.","Share your breakthroughs with the Tribe"]},{"day":19,"subject":"Build Awareness of Your Current Reality","summary":["To shift your identity, be clear on where you are with your current identity","For your identity reality check, ask deep questions so you can find good quality answers","Have gratitude for your current identity. This version of you has gotten you to this point"],"journal":["Ask yourself questions about who you really are,right here, right now, today.","For example:-Who do you believe that you are right now?-What are you capable of currently?-What do you believe you’re not capable of?-What are you doing or not doing every single day to move your manifestation forward?-What parts of yourself do you desire to upgrade?-What are you currently tolerating that you no longer desire to tolerate anymore?","What is now different in the essence of your being?","Note: Do your Soul Work before listening to the activation."],"meditation":"12_recalibrar_and_shift_your_identity_es.txt"},{"day":20,"subject":"Anchor in Your Highest Self","summary":[" When you embody your highest self, don’t just do it from the mind. Show up as your highest self","A version of you across time and space has already manifested what you desire","When you connect with that version of you, bring it from your future into the right now"],"journal":["Do the highest self-recalibration process"," Connect with your highest version and ask yourself specific questions like: -What does your highest version think?-How does this version of you walk and talk?-How does this version of you show up in your daily life?-What’s different about this version versus who you are right now?-What does this version no longer tolerate?","Note: Do your Soul Work for both of your manifestations.","After you’ve embodied your highest self through your Activation, write a minimum of three things you’re now committed to"],"meditation":"13_anchor_your_highest_self_into_the_es.txt"},{"day":21,"subject":"Quantum Inner Child Healing","summary":["You have an inner child: a little girl or a boy deep inside your consciousness","The patterns of your inner child can show up in your behavior and slow down your manifestations","Your inner child must be on board with what you want to manifest"],"journal":["After you’ve completed your activation, write down what came up for you during the process","Share your insights with the Tribe"," Note: Listen to the activation for both your Quest and your long-term manifestation"],"meditation":"14_quantum_inner_child_healing_es.txt"},{"day":22,"subject":"Collapse Timelines That Don’t Serve You","summary":["Timelines are blueprints of information linked to timestamps within the time and space paradox"," If you don’t update your timelines to be in sync with your intentions, it’ll take you longer to manifest"," When you shift your timelines, you allow yourself to become a different version of yourself"],"journal":["After you’ve completed your Activation, notice what now feels different about your manifestations","Share your breakthroughs with the Tribe"],"meditation":"15_collapse_timelines_that_dont_serve_es.txt"},{"day":23,"subject":"Accelerate the Manifestation Process","summary":["You can shift your relationship with time to create the desired results"," If you want to change your results with time, change your actions. But before that, change your belief system"],"journal":["Ask yourself, what do you currently believe about time?. Write down what the stories are that you find yourself telling yourself around time","Look at each of the limits that you wrote down and craft a minimum of three new beliefs","Listen to the activation from the previous lesson again. Notice what is different."],"meditation":"16_collapse_2_timelines_that_dont_es.txt"},{"day":24,"subject":"Pulling Your Reality Into the Now","summary":["You’ve been doing the 80% internally, but don’t forget about the other 20%. You must take action","Take aligned action from a frequency of “knowing,” as if your manifestation were already here"],"journal":["After listening to the Activation, notice what now feels different","What are three separate action items you commit to taking today to accelerate your manifestation into the now?","Share your breakthroughs with the Tribe","Note: Listen to the activation for both your Quest and your long-term manifestation"],"meditation":"17_map_the_energetics_of_the_sunset_es.txt"},{"day":25,"subject":"Future Gratitude Rewire","summary":["If you're not grateful for where you're currently at, you will slow down your manifestation","To be in a more natural state of gratitude, take the specific gratitude from your future and map it into the now"],"journal":["Write a list of at least 20 things you are grateful for specifically relating to your two manifestations","Share your breakthroughs with the Tribe"],"meditation":"18_tap_into_gratitude_es.txt"},{"day":26,"subject":"Your Daily Manifestation Practice","summary":[" Manifestation is a lifestyle. It is a way of being that you integrate into your life"," Life is the actual ceremony. Use your manifestation ritual to set yourself up for the rest of your day and beyond"],"journal":["Write down what you commit to daily after this Quest","Do your very first manifestation ritual","Share it with the Tribe","Post a photo or a video of your manifestation ritual on social media"]},{"day":27,"subject":"Lock In Your Committed Future","summary":[" When it comes to your manifestations, celebrate the big wins and the micro ones","The energy of celebration brings in more of what you desire. Make sure you are celebrating daily"," Remember, it's not about the destination. It's about the steps you get to walk along the way"],"journal":["Ask Yourself: What are you celebrating right now? What are you celebrating after this Quest?","Share it with the Tribe and on your social media","If you feel called for it and if it feels aligned,repeat this Quest and start again from Lesson 1"],"meditation":"19_integrate_everything_es.txt"}]}
    """.trimIndent()

        val horizontalMargin = dimensionResource(R.dimen.margin_horizontal)

        val journalState = JournalState(
            manifestationId = 2,
            manifestationSlug = "Second",
            manifestationMenuItems = arrayOf("First", "Second", ""),
            lessonDayItems = arrayOf("1","2","3","4","5"),
            manifestations = PreviewHelper.getManifestationFromJSON(manifestationsJson)!!,
            lessons = PreviewHelper.getLessonWrapperFromJSON(lessonsJson)!!,
            categoryColor = { _, _ -> grayLight },
            onItemDeleteClick = {},
            dateDifference = { _, _ -> 0L },
            onFetchManifestationWithLessons = { },
            onFetchJournal = {},
            onSaveLesson = { _, _ -> },
            onSaveButtonClick = {_, _ -> },
            onLongPress = {}
        )

        ManifestationsTheme {

            Surface (modifier = Modifier.fillMaxSize()) {
                JournalForm(
                    manifestationSlug = journalState.manifestationSlug!!,
                    manifestationMenuItems = journalState.manifestationMenuItems,
                    lessonDayItems = journalState.lessonDayItems,
                    manifestations = journalState.manifestations,
                    lessons = journalState.lessons,
                    onSaveLesson = { _, _ -> },
                    onSaveButtonClick =  { _, _ -> },
                    modifier = Modifier.padding(horizontal = horizontalMargin)
                )
            }

        }
    }
}


private const val TAG =  "che.JournalScreen"