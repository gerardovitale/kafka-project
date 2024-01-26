package dotenv

import org.scalatest.funsuite.AnyFunSuite

class DotenvSpec extends AnyFunSuite {
  test("loadDotenv") {
    val actualEnvVariables = DotEnv.loadEnvFromFile(".env.test")
    assert(actualEnvVariables.get("TEST_VARIABLE").contains("test_value"))
  }
}
