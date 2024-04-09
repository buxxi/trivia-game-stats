<script setup>
    let props = defineProps(['correct', 'incorrect', 'unanswered']);
    let total = props.correct + props.incorrect + props.unanswered;
    let correctPercentage = Math.ceil((props.correct / total) * 100);
    let incorrectPercentage = Math.ceil((props.incorrect / total) * 100);
    let unansweredPercentage = Math.ceil((props.unanswered / total) * 100);
</script>


<template>
    <div class="guess-count">
        <div class="correct" :title="'Correct: ' + correct + ' (' + correctPercentage + '%)'" :style="'flex-basis:' + correctPercentage + '%'"></div>
        <div class="incorrect" :title="'Incorrect: ' + incorrect + ' (' + incorrectPercentage + '%)'" :style="'flex-basis:' + incorrectPercentage + '%'"></div>
        <div class="unanswered" :title="'Unanswered: ' + incorrect + ' (' + unansweredPercentage + '%)'" :style="'flex-basis:' + unansweredPercentage + '%'"></div>
    </div>
</template>

<style>
    .guess-count {
        display: flex;
        height : 1em;
        border-radius: 1em;
        overflow: hidden;
        border: 1px solid lightgray;
    }

    .guess-count div {
        height : 1em;
        display : block;
    }

    .correct {
        background-color: greenyellow;
    }

    .incorrect {
        background-color: orangered;
    }

    .unanswered {
        background-color: gray;
    }
</style>