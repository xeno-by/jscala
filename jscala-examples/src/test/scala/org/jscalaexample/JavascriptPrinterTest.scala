package org.jscalaexample

import org.scalatest.FunSuite
import org.jscala._
import org.jscala.{javascript=>js}

class JavascriptPrinterTest extends FunSuite {
  test("String escaping") {
    val ast = js {
      val quote1 = """&copy; "something""""
      val quote2 = "&copy; \"something\""
      val multiline1 = "a\nb\tc"
      val multiline2 =
        """a
          b
          c"""
    }
    assert(ast.asString ===
      """{
        |  var quote1 = "&copy; \"something\"";
        |  var quote2 = "&copy; \"something\"";
        |  var multiline1 = "a\nb\tc";
        |  var multiline2 = "a\n          b\n          c";
        |}""".stripMargin)
    ast.eval()
  }

  test("Printer") {
    val ast = js {
      val a = Array("1", "2", "3")
      for (i <- a) console.log(i)
    }
    assert(ast.asString === """{
                            |  var a = ["1", "2", "3"];
                            |  for (var iIdx = 0, i = a[iIdx]; iIdx < a.length; i = a[++iIdx]) console.log(i);
                            |}""".stripMargin)
  }

  test("IIFE") {
    val ast = js {
      (() => {
        val a = 1
        console.log(a)
      })()
    }
    assert(ast.asString === """(function () {
                              |    var a = 1;
                              |    console.log(a);
                              |  })()""".stripMargin)
  }

  test("YUI Compressor") {
    val ast = js {
      val a = Array("1", "2", "3")
      for (i <- a) console.log(i)
    }
    assert(ast.compress === """var a=["1","2","3"];
                           |for(var iIdx=0,i=a[iIdx];
                           |iIdx<a.length;
                           |i=a[++iIdx]){console.log(i)
                           |};""".stripMargin)
  }

  test("Ternary operator") {
    val ast = js { if ((Math.PI > 3) && (Math.PI < 4)) Math.PI else Math.E }
    assert(ast.asString === "((Math.PI > 3) && (Math.PI < 4)) ? Math.PI : Math.E")
  }

  test("Switches") {
    val ast = js {
      val a = 1 match {
        case 1 | 2 => 1
      }
    }
    assert(ast.asString ===
      """{
        |  var a;
        |  switch (1) {
        |    case 1:
        |    case 2:
        |      a = 1;
        |      break;
        |  };
        |}""".stripMargin)
  }
}
