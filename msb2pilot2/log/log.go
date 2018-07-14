package log

import (
	"encoding/json"
	"fmt"
	"github.com/astaxie/beego/logs"
	"msb2pilot/util"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

type ConsoleCfg struct {
	Level int `json:"level"`
}

type FileCfg struct {
	FileName string `json:"filename"`
	Level    int    `json:"level"`
	MaxLines int    `json:"maxlines"`
	MaxSize  int    `josn:"maxsize"`
	Daily    bool   `json:"daily"`
	MaxDays  int    `json:"maxdays"`
	Rotate   bool   `json:"rotate"`
}

type Cfg struct {
	Console ConsoleCfg `json:"console"`
	File    FileCfg    `json:"file"`
}

const (
	cfgFileName         = "log.yml"
	defaultConsoleLevel = "Warn"
	defaultFileLevel    = "Info"
)

var (
	Log         *logs.BeeLogger
	loggerLevel = map[string]int{"Emergency": 0, "Alert": 1, "Critical": 2, "Error": 3, "Warn": 4, "Notice": 5, "Info": 6, "Debug": 7}
)

func init() {
	Log = logs.NewLogger()
	Log.EnableFuncCallDepth(true)

	cfg := getConfig()
	setLogger(logs.AdapterConsole, &cfg.Console)
	checkLogDir(cfg.File.FileName)
	setLogger(logs.AdapterFile, &cfg.File)
}

func setLogger(adapter string, cfg interface{}) bool {
	b, err := json.Marshal(cfg)
	if err != nil {
		fmt.Printf(" cfg json trans error: %s\n", adapter, err.Error())
		return false
	}

	err = Log.SetLogger(adapter, string(b))
	if err != nil {
		fmt.Printf("set %s failed: %s\n", adapter, err.Error())
		return false
	}

	return true

}

func checkLogDir(path string) {
	if path == "" {
		return
	}

	var index int
	pathSep := string(os.PathSeparator)
	if index = strings.LastIndex(path, pathSep); index <= 2 {
		return
	}

	perm, _ := strconv.ParseInt("0660", 8, 64)
	if err := os.MkdirAll(path[0:index], os.FileMode(perm)); err != nil {
		return
	}
}

func loadCustom() map[string]interface{} {
	fullPath := filepath.Join(util.GetCfgPath(), cfgFileName)
	fmt.Println("log config path is:" + fullPath)
	config, err := util.Read(fullPath)
	if err != nil {
		fmt.Println("read config file error")
		return nil
	}

	cfg := make(map[string]interface{})
	err = util.UnmarshalYaml(config, &cfg)

	if err != nil {
		fmt.Printf("parse config file error: %s\n", err.Error())
		return nil
	}
	return cfg
}

func getConfig() (result *Cfg) {
	result = getDefaultCfg()

	customs := loadCustom()

	if customs == nil {
		return
	}

	var console map[interface{}]interface{}
	if cons, exist := customs["console"]; exist {
		console = cons.(map[interface{}]interface{})

		if levelstr, exist := console["level"]; exist {
			if level, ok := loggerLevel[levelstr.(string)]; ok {
				result.Console.Level = level
			}
		}
	}
	var file map[interface{}]interface{}
	if f, exist := customs["file"]; !exist {
		return
	} else {
		file = f.(map[interface{}]interface{})
	}

	if filename, e := file["filename"]; e {
		result.File.FileName = filename.(string)
	}
	if levelstr, e := file["level"]; e {
		if level, exist := loggerLevel[levelstr.(string)]; exist {
			result.File.Level = level
		}
	}
	if maxlines, e := file["maxlines"]; e {
		result.File.MaxLines = maxlines.(int)
	}

	if maxsize, e := file["maxsize"]; e {
		result.File.MaxSize = maxsize.(int) * 1024 * 1024
	}

	if daily, e := file["daily"]; e {
		result.File.Daily = daily.(bool)
	}

	if maxdays, e := file["maxdays"]; e {
		result.File.MaxDays = maxdays.(int)
	}

	if rotate, e := file["rotate"]; e {
		result.File.Rotate = rotate.(bool)
	}

	return
}

func getDefaultCfg() *Cfg {
	return &Cfg{
		Console: ConsoleCfg{
			Level: loggerLevel[defaultConsoleLevel],
		},
		File: FileCfg{
			FileName: "msb2pilot.log",
			Level:    loggerLevel[defaultFileLevel],
			MaxLines: 300000,
			MaxSize:  30 * 1024 * 1024,
			Daily:    true,
			MaxDays:  10,
			Rotate:   true,
		},
	}
}
