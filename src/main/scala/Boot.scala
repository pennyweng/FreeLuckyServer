package com.jookershop.freelucky

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.ByteString
import spray.can.Http
import redis.RedisClient

import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.annotation._
import java.io._
import scala.concurrent.ExecutionContext.Implicits.global


object Boot {
	implicit val system = ActorSystem("on-spray-can")

	val mapper = new ObjectMapper()
	val redis = RedisClient("www.jookershop.com", 6379)
	// val redis = RedisClient("127.0.0.1", 6379)
	val service = system.actorOf(Props[DemoServiceActor], "demo-service")

	def main(args : Array[String]) {	
		initCategory()
	  	IO(Http) ! Http.Bind(service, "0.0.0.0", port = 8080)
	}

	def initCategory() {
		// del()
		new File("category").listFiles().foreach { file =>
			if (file.isFile()) {
				val cid = file.getName
				println("cid:" + cid)

				val lines = scala.io.Source.fromFile("category/" + file.getName, "utf-8").getLines.mkString
				// val source = scala.io.Source.fromFile("category/" + file.getName)
				// val lines = source.mkString
				// source.close()
				// println("lines:" + lines)
				val categories = mapper.readValue[Array[CategoryJ]](lines, classOf[Array[CategoryJ]])
				categories.foreach { category =>
					redis.llen(category.id).map { part =>
						val cs = mapper.writeValueAsString(CategoryJ(category.id, category.title, 
                      		category.desc, part, category.target, category.img, category.opendate))
						redis.hset("category:" + cid, category.id, cs)	
					}
				}
			}
		}

	}

	def del() {
		redis.del("category:0")
		redis.del("category:1")
		redis.del("category:2")
		redis.del("category:3")
		redis.del("user")
	}
}