package com.jookershop.freelucky

import scala.concurrent.duration._

import akka.actor.{Props, Actor}
import akka.pattern.ask

import spray.routing.{HttpService, RequestContext}
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import spray.http.StatusCode

import MediaTypes._
import CachingDirectives._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try,Success,Failure}
import scala.concurrent.{ future, promise }
import scala.collection.mutable._

import com.jookershop.freelucky.Boot
import com.jookershop.freelucky.Consts._

import akka.util.ByteString

import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.annotation._

case class CategoryJ @JsonCreator() (@scala.reflect.BeanProperty @JsonProperty("id") id : String,  
  @scala.reflect.BeanProperty @JsonProperty("title") title : String, 
  @scala.reflect.BeanProperty @JsonProperty("desc") desc: String, 
  @scala.reflect.BeanProperty @JsonProperty("participator") participator: Long, 
  @scala.reflect.BeanProperty @JsonProperty("target") target : Long, 
  @scala.reflect.BeanProperty @JsonProperty("img") img : String, 
  @scala.reflect.BeanProperty @JsonProperty("opendate") opendate : Long) 

case class History @JsonCreator() (@scala.reflect.BeanProperty @JsonProperty("id") id : String,  
  @scala.reflect.BeanProperty @JsonProperty("title") title : String, 
  @scala.reflect.BeanProperty @JsonProperty("num") num: Long, 
  @scala.reflect.BeanProperty @JsonProperty("ts") ts : Long) 

case class Win @JsonCreator() (@scala.reflect.BeanProperty @JsonProperty("id") id : String,  
  @scala.reflect.BeanProperty @JsonProperty("title") title : String, 
  @scala.reflect.BeanProperty @JsonProperty("opendate") opendate: Long, 
  @scala.reflect.BeanProperty @JsonProperty("win_num") win_num : Long,
  @scala.reflect.BeanProperty @JsonProperty("p") p : String
  ) 

class DemoServiceActor extends Actor with DemoService {
  def actorRefFactory = context
  def receive = runRoute(demoRoute)
}

trait DemoService extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher

  val demoRoute = {
    get {
      path("play") { 
        parameters('cid.as[String], 'id.as[String], 'p.as[String])  { (cid, id, p) =>
          val res = Boot.redis.hget("user", p).flatMap  { e =>
            val t1 = e.getOrElse(ByteString("0")).utf8String.toLong
            val tt = System.currentTimeMillis - t1

            if(tt > JOIN_INTERVAL_TIME) {
              val now = System.currentTimeMillis
              Boot.redis.hset("user", p, now)

              for{
                part <- Boot.redis.rpush(id, p)
                c <- Boot.redis.hget("category:" + cid, id)
                h <- Boot.redis.hget("history", p)
              } yield {
                c.map { categorybyteString =>
                    val category = Boot.mapper.readValue[CategoryJ](categorybyteString.utf8String, classOf[CategoryJ])
                    val cs = Boot.mapper.writeValueAsString(CategoryJ(category.id, category.title, 
                      category.desc, part, category.target, category.img, category.opendate))
                    Boot.redis.hset("category:" + cid, id, cs)
                    
                    h match {
                      case Some(s) =>
                        val histories = Boot.mapper.readValue[Array[History]](s.utf8String, classOf[Array[History]])
                        val nHistory = new ListBuffer[History]()
                        nHistory.appendAll(histories)
                        nHistory.append(History(category.id, category.title, part, now))
                        Boot.redis.hset("history", p, Boot.mapper.writeValueAsString(nHistory.toArray)) 
                      case None =>
                        Boot.redis.hset("history", p, Boot.mapper.writeValueAsString( List(History(category.id, category.title, part, now)).toArray))
                    }    
                }

                HttpResponse(StatusCodes.OK, part + "")
              }
            } else {
              future (HttpResponse(StatusCodes.BandwidthLimitExceeded, JOIN_INTERVAL_TIME - (System.currentTimeMillis - t1) + ""))
            }
          }

          complete(res)
      }} ~
      path("category" / "[^/]+".r ) { cid =>
        val res = Boot.redis.hgetall("category:" + cid).map { c =>
          c match {
            case Success(s) => "[" + s.values.map{ _.utf8String}.mkString(",") + "]"
            case Failure(f) => "[]"
          }
        }
        complete(res)
      } ~
      path("categorymenu") { 
        val res = "3C,餐廳,住宿"
        complete(res)
      } ~
      pathPrefix("img") {
        getFromDirectory("images")
      } ~
      pathPrefix("rule") {
        getFromDirectory("gamerule")
      } ~      
      path("history") {
        parameters('p.as[String])  { p =>
          val res = Boot.redis.hget("history", p).map { e =>
            e match {
              case Some(s) => s.utf8String
              case None => "[]"
            }
          }

          complete(res)
        }
      } ~      
      path("check") {
        parameters('id.as[String], 'p.as[String])  { (id, p) =>
          val res = Boot.redis.hget("user", p).map  { e =>
            val t1 = e.getOrElse(ByteString("0")).utf8String.toLong
            val tt = System.currentTimeMillis - t1

            if(tt > JOIN_INTERVAL_TIME) {
              HttpResponse(StatusCodes.OK, "")
            } else {
              HttpResponse(StatusCodes.BandwidthLimitExceeded, JOIN_INTERVAL_TIME - (System.currentTimeMillis - t1) + "")
            }
          }
          complete(res)
        }
      } ~      
      path("winlist") {
        // val res = """ [
        //     {
        //         "id": "a1",
        //         "title": "Samsung Galaxy Mega (I9200) 6.3吋雙核智慧機",
        //         "opendate": 1377196848,
        //         "win_num": 33323,
        //         "win_phone": "0983xxx718"
        //     },
        //     {
        //         "id": "a2",
        //         "title": "【台北國賓大飯店】明園西餐廳2人自助式午或晚餐(一套2張)",
        //         "opendate": 1377196848,
        //         "win_num": 99383,
        //         "win_phone": "0983xxx718"
        //     }
        // ]"""

        val res = """[]"""
        complete(res)
      }

    }
  }

}
