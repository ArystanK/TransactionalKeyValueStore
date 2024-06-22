package kz.arctan.transactional_key_value_store.domain.utils

import platform.Foundation.NSUUID

actual typealias MyUUID = NSUUID

actual fun createUUID(): MyUUID = NSUUID.UUID()
