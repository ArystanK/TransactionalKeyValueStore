package kz.arctan.transactional_key_value_store.domain.utils

import java.util.UUID

actual typealias MyUUID = UUID

actual fun createUUID(): MyUUID = UUID.randomUUID()
