Project Manager – Installation Instructions
===========================================

Requirements:
-------------
- Linux system (any modern distribution)
- The `ProjectManager` file must have executable permissions

Installation:
-------------
1. Download and extract the archive:

   tar -xzf project-manager-1.0.0.tar.gz

2. (Optional but recommended) Verify the SHA256 checksum:

   echo "5468fc0117d6f04d3e5ff835e7f5712b5cbfaa8dee428b83eb82eaa15e60a39b  project-manager-1.0.0.tar.gz" | sha256sum -c -

   The output should be:

   `project-manager-1.0.0.tar.gz: OK`

3. Change to the extracted directory:

   cd project-manager-1.0.0

4. Make the application executable (if needed):

   chmod +x ProjectManager

5. Run the application:

   ./ProjectManager

Notes:
------
- If you get a "Permission denied" error, check file permissions.
- Requires a graphical environment (X11 or Wayland).


------------------------------------------------------------

Project Manager – Instrukcja instalacji
=======================================

Wymagania:
----------
- System Linux (dowolna nowoczesna dystrybucja)
- Plik `ProjectManager` musi mieć nadane uprawnienia wykonywalne

Instalacja:
-----------
1. Pobierz i rozpakuj archiwum:

   tar -xzf project-manager-1.0.0.tar.gz

2. (Opcjonalnie, ale zalecane) Sprawdź sumę kontrolną SHA256:

   echo "5468fc0117d6f04d3e5ff835e7f5712b5cbfaa8dee428b83eb82eaa15e60a39b  project-manager-1.0.0.tar.gz" | sha256sum -c -

   Powinien pojawić się komunikat:

   `project-manager-1.0.0.tar.gz: OK`

3. Przejdź do wypakowanego katalogu:

   cd project-manager-1.0.0

4. Nadaj aplikacji uprawnienia do uruchamiania (jeśli to konieczne):

   chmod +x ProjectManager

5. Uruchom aplikację:

   ./ProjectManager

Uwagi:
------
- Jeśli pojawi się błąd „Permission denied”, sprawdź uprawnienia pliku.
- Wymagane jest środowisko graficzne (X11 lub Wayland).
