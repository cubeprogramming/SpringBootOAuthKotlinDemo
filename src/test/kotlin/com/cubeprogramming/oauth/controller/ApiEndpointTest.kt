package com.cubeprogramming.oauth.controller

import com.cubeprogramming.oauth.GitHubOAuthApp
import com.google.common.collect.Lists
import com.google.api.gax.paging.Page
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.IOException
import java.lang.invoke.MethodHandles
import java.nio.file.Files
import java.nio.file.Paths


@ActiveProfiles("local")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [GitHubOAuthApp::class], webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) //Clears the context cache after each test method
internal class ApiEndpointTest {

    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    /*@SpringBootApplication
  @ComponentScan(basePackages = "no.sysco.cip")
  class Config {

  }*/
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Value("\${server.port}")
    private lateinit var port: String

    private lateinit var token: AccessToken

    @BeforeEach
    fun setUp() {
        authExplicit("global-ace-352220-bc375b035377.json")
    }

    @Test
    fun `Test getting user data`() {
        val url = "http://localhost:$port/user"

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token.tokenValue)
//        headers["X-XSRF-TOKEN"] = token.tokenValue

        val response: ResponseEntity<String> = restTemplate.exchange<String>(
            url, HttpMethod.GET, HttpEntity<String>(headers),
            String::class.java
        )
        Assertions.assertThat(response.body!!.contains("name")).isEqualTo(true)
    }

    @Throws(IOException::class)
    fun authExplicit(jsonPath: String) {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        val jsonResource = javaClass.classLoader.getResource(jsonPath)
        Assertions.assertThat(jsonResource).isNotNull().withFailMessage("Key file $jsonPath can not be found")

        val keyPath = Paths.get(jsonResource!!.toURI())
        val keyData = Files.newInputStream(keyPath)

//        val credentials: GoogleCredentials = GoogleCredentials.getApplicationDefault()
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(keyData)
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"))
        credentials.refreshIfExpired()
        token = credentials.accessToken
        /*val storage: Storage = StorageOptions.newBuilder().setCredentials(credentials).build().service
        println("Buckets:")
        val buckets: Page<Bucket> = storage.list()
        for (bucket in buckets.iterateAll()) {
//            println(bucket)
            log.info(bucket.toString())
        }*/
    }
}