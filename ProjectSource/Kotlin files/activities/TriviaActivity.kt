
package com.example.factZAP.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.factZAP.R
import com.example.factZAP.data_classes.Result
import com.example.factZAP.data_classes.TriviaQuestion
import com.example.factZAP.databinding.ActivityTriviaBinding
import com.example.factZAP.utilities.Constants
import java.util.Locale

/**
 * Activity that handles the main trivia game functionality.
 */
class TriviaActivity : AppCompatActivity() {

    /** View binding instance for accessing the activity's views */
    private var binding: ActivityTriviaBinding? = null

    /** Current question score */
    private var score = 0

    /** Countdown timer for each question */
    private var timer: CountDownTimer? = null

    /** Remaining time for current question in seconds */
    private var timeRemaining = 0

    /** Flag indicating if answer selection is allowed */
    private var play = true

    /** Current question index */
    private var index = 0

    /** List of results for each answered question */
    private var results = ArrayList<Result>()

    /** Correct answer for current question */
    private lateinit var rightAnswer: String

    /** Randomized answer options for current question */
    private lateinit var options: List<String>

    /** List of trivia questions for the game */
    private lateinit var questions: ArrayList<TriviaQuestion>

    /**
     * Initializes the game activity and sets up UI components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriviaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        questions = intent.getSerializableExtraProvider<ArrayList<TriviaQuestion>>("questionList")
            ?: arrayListOf()

        setQuestions()
        setOptions()
        startTimer()

        binding?.questionNumber?.text =
            binding?.root?.context?.getString(R.string.question_text, 1, questions.size)
        binding?.nextButton?.setOnClickListener {
            onNext()
        }

        val redButtonBackground = ContextCompat.getDrawable(
            this, R.drawable.red_button_background
        )

        val optionListener = OnClickListener { view ->
            if (play) {
                timer?.cancel()
                view.background = redButtonBackground
                displayAnswer()
                setScore(view as Button?)
                play = false
            }

        }

        binding?.optionA?.setOnClickListener(optionListener)
        binding?.optionB?.setOnClickListener(optionListener)
        binding?.optionC?.setOnClickListener(optionListener)
        binding?.optionD?.setOnClickListener(optionListener)

    }

    /**
     * sets the current question text in the UI.
     * Decodes HTML entities in the question text before display.
     */
    private fun setQuestions() {
        val questionDecoded = Constants.decodeString(questions[index].question)
        binding?.questionText?.text = questionDecoded
    }

    /**
     * sets up answer options for the current question.
     * handles both multiple choice and true/false question types.
     * randomizes option order and updates UI accordingly.
     */
    private fun setOptions() {
        val question = questions[index]
        val randomOptions =
            Constants.randomizeOptions(question.correctAnswer, question.incorrectAnswers)

        rightAnswer = randomOptions.first
        options = randomOptions.second

        binding?.optionA?.text = options[0]
        binding?.optionB?.text = options[1]

        if (question.type == "multiple") {
            binding?.optionC?.visibility = View.VISIBLE
            binding?.optionD?.visibility = View.VISIBLE
            binding?.optionC?.text = options[2]
            binding?.optionD?.text = options[3]
        } else {
            binding?.optionC?.visibility = View.GONE
            binding?.optionD?.visibility = View.GONE
        }

    }

    /**
     * Handles progression to the next question or ends the game.
     *
     * it records the result of the current question, updates the question index,
     * resets the game state for the next question, starts a new timer, and navigates
     * to the finish activity when all questions are answered.
     */
    private fun onNext() {

        val result = Result(
            10 - timeRemaining,
            questions[index].type,
            questions[index].difficulty,
            score
        )

        results.add(result)
        score = 0

        if (index < questions.size - 1) {
            timer?.cancel()
            index++
            setQuestions()
            setOptions()
            binding?.questionNumber?.text =
                binding?.root?.context?.getString(R.string.question_text, index + 1, questions.size)
            resetBackgroundColour()
            play = true
            startTimer()
        } else {
            endGame()
        }

    }

    /**
     * starts the timer for each question
     * it also updates the progress bar and time display, and
     * automatically displays correct answer when time runs out.
     */
    private fun startTimer() {
        binding?.timeProgressBar?.max = 10
        binding?.timeProgressBar?.progress = 10

        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(remainingTime: Long) {
                binding?.timeProgressBar?.incrementProgressBy(-1)
                binding?.timeLimit?.text = String.format(
                    Locale.getDefault(), "%d", remainingTime / 1000
                )
                timeRemaining = (remainingTime / 1000).toInt()
            }

            override fun onFinish() {
                displayAnswer()
                play = false
            }

        }.start()
    }

    /**
     * Calculates score for current question based on selected answer.
     *
     * @param button The selected answer button
     */
    private fun setScore(button: Button?) {
        if (rightAnswer == button?.text) {
            score = getScore()
        }
    }

    /**
     * Calculates score based on question difficulty, type, and remaining time.
     *
     * Scoring factors:
     * - Difficulty (easy=1, medium=2, hard=3)
     * - Question type (boolean=1, multiple=2)
     * - Time remaining (proportional bonus)
     *
     * @return Calculated score for the current question
     */
    private fun getScore(): Int {
        val difficultyScore = when (questions[index].difficulty) {
            "easy" -> 1
            "medium" -> 2
            else -> 3
        }
        val questionTypeScore = when (questions[index].type) {
            "boolean" -> 1
            else -> 2
        }
        val timeRemainingScore: Int = (timeRemaining) / (10)
        return difficultyScore + questionTypeScore + timeRemainingScore
    }

    /**
     * Highlights the correct answer in the UI.
     * Changes background color of correct answer button to yellow.
     */
    private fun displayAnswer() {
        val yellowBackground = ContextCompat.getDrawable(
            this, R.drawable.yellow_correct_button_background
        )
        when (true) {
            (rightAnswer == options[0]) -> binding?.optionA?.background = yellowBackground
            (rightAnswer == options[1]) -> binding?.optionB?.background = yellowBackground
            (rightAnswer == options[2]) -> binding?.optionC?.background = yellowBackground
            else -> binding?.optionD?.background = yellowBackground
        }
    }

    /**
     * Resets all answer button backgrounds to default color.
     * Called when moving to next question.
     */
    private fun resetBackgroundColour() {
        val defaultBackground = ContextCompat.getDrawable(
            this, R.drawable.button_gray_background
        )

        binding?.optionA?.background = defaultBackground
        binding?.optionB?.background = defaultBackground
        binding?.optionC?.background = defaultBackground
        binding?.optionD?.background = defaultBackground

    }

    /**
     * Ends the game and transitions to results screen.
     * passes all question results to FinishActivity.
     */
    private fun endGame() {
        val intent = Intent(this, FinishActivity::class.java)
        intent.putExtra("resultList", results)
        startActivity(intent)
        finish()
    }

    /**
     * Extension function to safely retrieve serializable extras from Intent.
     *
     * @param identifierParameter Key used to store the extra
     * @return The extra cast to type T, or null if not found
     */
    private inline fun <reified T : java.io.Serializable> Intent.getSerializableExtraProvider(
        identifierParameter: String
    ): T? {
        return this.getSerializableExtra(identifierParameter, T::class.java)
    }
}