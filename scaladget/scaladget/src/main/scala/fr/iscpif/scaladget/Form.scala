/*
 * Copyright (C) 12/06/14 Mathieu Leclaire
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.iscpif.scaladget

import d3._
import d3mapping.Selection
import org.scalajs.jquery
import scala.Enumeration
import DomUtil._
import jquery._

import scala.scalajs.js.JSON

object Form {
  private def button(selection: Selection, id: String, label: String, clazz: String, extraAttr: Tuple2[String, String]*) = {
    val button = selection.button.id(id).tyype("button").clazz("btn " + clazz)
    extraAttr.foreach { case (att, value) => button.attr(att, value)}
    button.html(label)
  }

  object State extends Enumeration {

    class State(val name: String) extends Val(name)

    val DEFAULT = new State("default")
    val PRIMARY = new State("primary")
    val INFO = new State("info")
    val SUCCESS = new State("success")
    val WARNING = new State("warning")
    val ERROR = new State("error")
    val LINK = new State("link")
  }

}

import Form._
import Form.State._

/*nbCol is the number of columns dedicated for each element of the line.
 The sum is 12. Ex: line(8,4): the first element is 2/3 of the line space.
 0 for no indication at all
 */
protected case class Form(root: Selection, selection: Selection, id: String,componentIds: List[String] = List()) extends WComposer {

  implicit def selectionToForm(s: Any): Form = this

  private def column(colIndice: Int): Selection = {
    if (colIndice > 0) selection.div.clazz("col-md-" + colIndice).div.clazz("form-group")
    else selection
  }

  def line: Form = copy(selection = root.div.clazz("row"))

  def group: Form = copy(selection = root.div.clazz("btn-group"))

  def well: Form = {
    val w = root.div.clazz("well")
    copy(root = w, selection = w)
  }

  def input(id: String, default: String, init: String, nbCol: Int = 12): Form = {
    column(nbCol)
      .input.clazz("form-control input-lg")
      .tyype("text").placeholder(default).id(id).value(init)
    copy(componentIds = componentIds :+ id)
  }

  def button(id: String, label: String, state: State = DEFAULT, nbCol: Int = 0): Form = Form.button(column(nbCol), id, label, "btn-" + state.name + " btn-lg")

  def button(id: String, label: String, nbCol: Int): Form = Form.button(column(nbCol), id, label, "btn-default")

  def label(title: String, state: State = DEFAULT, nbCol: Int = 12): Form = column(nbCol).append("span").attr("class", "label label-" + state.name).html(title)

  def dropdown(id: String, label: String, nbCol: Int, actions: Tuple2[String, String]*): Form = {
    val sel = column(nbCol).div.clazz("btn-group-lg")
    Form.button(sel, id, label + "<span class=\"caret\"></span>", "btn-default dropdown-toggle", ("data-toggle", "dropdown"))
    val ul = sel.ul.clazz("dropdown-menu").role("menu")
    actions.foreach { case (id, name) => ul.insert("li", "ul").insert("a", "li").id("id").href("#").html(name)}
    selection
  }

  def toJSON = "{\n" +
    componentIds.map{ i=>
    "\""+i+"\":\""+ jQuery("#"+i).`val`() + "\""
  }.mkString("\n") + "\n}"
  
  //  def table(header: Seq[String], lines: Seq[String]*) = selection.
}