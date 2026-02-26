package com.esba.ahorroscompartidos.data.repository

import com.esba.ahorroscompartidos.data.local.dao.BankDao
import com.esba.ahorroscompartidos.data.mapper.*
import com.esba.ahorroscompartidos.data.remote.FirebaseAuthService
import com.esba.ahorroscompartidos.data.remote.FirebaseFirestoreService
import com.esba.ahorroscompartidos.domain.model.*
import com.esba.ahorroscompartidos.domain.repository.BankRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject

class BankRepositoryImpl @Inject constructor(
    private val dao: BankDao,
    private val authService: FirebaseAuthService,
    private val firestore: FirebaseFirestoreService
) : BankRepository {

    private val mutex = Mutex()

    override fun observeUserAccount(): Flow<UserAccount> =
        dao.observeUser().map { it.toDomain() }

    override fun observeSharedAccount(): Flow<SharedAccount> =
        dao.observeShared().map { it.toDomain() }

    override fun observeTransactions(): Flow<List<Transaction>> =
        dao.observeTransactions().map { list -> list.map { it.toDomain() } }

    override suspend fun transferToShared(amount: Double) {
        mutex.withLock {

            val user = dao.observeUser().first()
            val shared = dao.observeShared().first()

            if (user.personalBalance < amount)
                throw Exception("Saldo insuficiente")

            val transaction = createTransaction(
                user.id,
                TransactionType.TRANSFER_TO_SHARED,
                amount
            )

            dao.updateUser(user.copy(personalBalance = user.personalBalance - amount))
            dao.updateShared(shared.copy(balance = shared.balance + amount))
            dao.insertTransaction(transaction.toEntity())

            try {

                syncWithFirebase(
                    user.id,
                    user.personalBalance - amount,
                    shared.balance + amount,
                    transaction
                )

                confirmTransaction(transaction)

            } catch (e: Exception) {

                rollback(user, shared, transaction)
                throw e
            }
        }
    }

    override suspend fun withdrawFromShared(amount: Double) {
        mutex.withLock {

            val user = dao.observeUser().first()
            val shared = dao.observeShared().first()

            if (shared.balance < amount)
                throw Exception("Saldo compartido insuficiente")

            val transaction = createTransaction(
                user.id,
                TransactionType.WITHDRAW_SHARED,
                amount
            )

            dao.updateUser(user.copy(personalBalance = user.personalBalance + amount))
            dao.updateShared(shared.copy(balance = shared.balance - amount))
            dao.insertTransaction(transaction.toEntity())

            try {

                syncWithFirebase(
                    user.id,
                    user.personalBalance + amount,
                    shared.balance - amount,
                    transaction
                )

                confirmTransaction(transaction)

            } catch (e: Exception) {

                rollback(user, shared, transaction)
                throw e
            }
        }
    }

    override suspend fun depositPersonal(amount: Double) {
        mutex.withLock {

            val user = dao.observeUser().first()
            val shared = dao.observeShared().first()

            val transaction = createTransaction(
                user.id,
                TransactionType.DEPOSIT_PERSONAL,
                amount
            )

            dao.updateUser(user.copy(personalBalance = user.personalBalance + amount))
            dao.insertTransaction(transaction.toEntity())

            try {

                syncWithFirebase(
                    user.id,
                    user.personalBalance + amount,
                    shared.balance,
                    transaction
                )

                confirmTransaction(transaction)

            } catch (e: Exception) {

                rollback(user, shared, transaction)
                throw e
            }
        }
    }

    override suspend fun withdrawPersonal(amount: Double) {
        mutex.withLock {

            val user = dao.observeUser().first()
            val shared = dao.observeShared().first()

            if (user.personalBalance < amount)
                throw Exception("Saldo personal insuficiente")

            val transaction = createTransaction(
                user.id,
                TransactionType.WITHDRAW_PERSONAL,
                amount
            )

            dao.updateUser(user.copy(personalBalance = user.personalBalance - amount))
            dao.insertTransaction(transaction.toEntity())

            try {

                syncWithFirebase(
                    user.id,
                    user.personalBalance - amount,
                    shared.balance,
                    transaction
                )

                confirmTransaction(transaction)

            } catch (e: Exception) {

                rollback(user, shared, transaction)
                throw e
            }
        }
    }

    private fun createTransaction(
        userId: String,
        type: TransactionType,
        amount: Double
    ) = Transaction(
        id = UUID.randomUUID().toString(),
        fromUserId = userId,
        type = type,
        amount = amount,
        timestamp = System.currentTimeMillis(),
        status = TransactionStatus.PENDING
    )

    private suspend fun syncWithFirebase(
        userId: String,
        newPersonal: Double,
        newShared: Double,
        transaction: Transaction
    ) {

        firestore.updateBalances(userId, newPersonal, newShared)

        firestore.addTransaction(
            mapOf(
                "id" to transaction.id,
                "fromUserId" to userId,
                "type" to transaction.type.name,
                "amount" to transaction.amount,
                "timestamp" to transaction.timestamp,
                "status" to TransactionStatus.CONFIRMED.name
            )
        )
    }

    private suspend fun confirmTransaction(transaction: Transaction) {
        dao.updateTransaction(
            transaction.copy(status = TransactionStatus.CONFIRMED).toEntity()
        )
    }

    private suspend fun rollback(
        originalUser: com.esba.ahorroscompartidos.data.local.entity.UserAccountEntity,
        originalShared: com.esba.ahorroscompartidos.data.local.entity.SharedAccountEntity,
        transaction: Transaction
    ) {
        dao.updateUser(originalUser)
        dao.updateShared(originalShared)
        dao.updateTransaction(
            transaction.copy(status = TransactionStatus.FAILED).toEntity()
        )
    }
}