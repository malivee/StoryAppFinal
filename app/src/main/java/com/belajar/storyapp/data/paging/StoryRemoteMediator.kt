package com.belajar.storyapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.belajar.storyapp.data.api.response.ListStoryItem
import com.belajar.storyapp.data.api.retrofit.ApiService
import com.belajar.storyapp.data.entity.RemoteKeys
import com.belajar.storyapp.data.room.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, ListStoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_INDEX_PAGE
            }

            LoadType.PREPEND -> {
                val remoteKeys = getFirstItemRemoteKeys(state)
                remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getLastItemRemoteKeys(state)
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val response = apiService.getStories(page, state.config.pageSize)
//            if (response.error == false) {
//
//            }
            val endOfPage = response.listStory?.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyDatabase.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPage == true) null else page + 1

                val key = response.listStory?.map {
                    it.id.let { id -> RemoteKeys(id = id, prevKey = prevKey, nextKey = nextKey) }
                }
                if (key != null) {
                    storyDatabase.remoteKeysDao().insertAll(key)
                }
                response.listStory?.let { storyDatabase.storyDao().insertStory(it) }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPage == true)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getFirstItemRemoteKeys(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            it.id?.let { id -> storyDatabase.remoteKeysDao().getRemoteKeys(id) }
        }

    }

    private suspend fun getLastItemRemoteKeys(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            it.id?.let { id -> storyDatabase.remoteKeysDao().getRemoteKeys(id) }
        }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.id?.let {
                storyDatabase.remoteKeysDao().getRemoteKeys(it)
            }

        }
    }


    companion object {
        private const val INITIAL_INDEX_PAGE = 1
    }


}