package main

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"log"
	"skyvillage-launcher-rewrite/settings"
	"skyvillage-launcher-rewrite/updater"
)

func main() {
	log.SetPrefix("SKYVILLAGE|> ")
	log.Printf("Starting launcher version %s by %s\n", LauncherVersion, Author)
	a := app.NewWithID("hu.skyvillage.launcher")

	settings.LoadSettings()

	if updater.CheckForLauncherUpdates() {
		err := updater.UpdateLauncher()
		if err != nil {
			a.SendNotification(fyne.NewNotification("Hiba!", "Hiba történt a kliens frissítése közben!"))
			log.Fatalf("Error updating launcher!\n%s", err.Error())
		}
	}


	InitMainWindow(a, func() {
		log.Println("Closing...")
		settings.Save()
	})
	defer CloseMainWindow()
}
