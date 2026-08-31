package com.dyor.habithero.domain.usecase

import com.dyor.habithero.domain.model.generation.GenerationInput
import com.dyor.habithero.domain.model.generation.GenerationInputBuilder
import com.dyor.habithero.domain.model.generation.GenerationOutput
import com.dyor.habithero.domain.model.generation.generationInput

/**
 * Abstraction over the AI backend that turns a [GenerationInput] into a [GenerationOutput].
 * Implemented per provider in `data/source/ai/` (e.g. Replicate, OpenAI) and bound in DI.
 */
interface AiGenerationProvider {
    suspend fun generate(input: GenerationInput): Result<GenerationOutput>
}

/** Convenience overload that builds the input inline with the [generationInput] DSL. */
suspend fun AiGenerationProvider.generate(block: GenerationInputBuilder.() -> Unit): Result<GenerationOutput> {
    val input = generationInput(block)
    return generate(input)
}
