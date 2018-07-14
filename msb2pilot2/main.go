package main

import (
	"msb2pilot/log"
	_ "msb2pilot2/routers"

	"github.com/astaxie/beego"
)

func main() {
	log.Log.Informational("**************** init msb2pilot ************************")
	beego.Run()
}
