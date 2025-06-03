# Description: This script automates the process of creating a pull request for a release branch.
gh pr create --base release --head main --title "App Release" --body "This is an automated release PR"