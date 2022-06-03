package com.cubeprogramming.oauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitHubOAuthApp

fun main(args: Array<String>) {
	runApplication<GitHubOAuthApp>(*args)
}
