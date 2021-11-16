package pl.exbook.exbook.statistics.domain

interface UserStatisticsRepository {

    fun save(userStatistics: UserStatistics): UserStatistics
}
