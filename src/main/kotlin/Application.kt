package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Item(val id: Int, val name: String)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        val items = mutableListOf(
            Item(1, "First item"),
            Item(2, "Second item"),
            Item(3, "Third item")
        )

        get("/items") {
            call.respond(items)
        }

        get("/items/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
                return@get
            }
            val item = items.find { it.id == id }
            if (item == null) {
                call.respond(HttpStatusCode.NotFound, "Item not found")
            } else {
                call.respond(item)
            }
        }

        post("/items") {
            val name = call.request.queryParameters["name"]
            if (name.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or empty 'name' query parameter")
                return@post
            }
            val newId = (items.maxOfOrNull { it.id } ?: 0) + 1
            val newItem = Item(newId, name)
            items.add(newItem)
            call.respond(HttpStatusCode.Created, newItem)
        }

        delete("/items/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
                return@delete
            }
            val removed = items.removeIf { it.id == id }
            if (removed) {
                call.respond(HttpStatusCode.OK, "Item deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Item not found")
            }
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}