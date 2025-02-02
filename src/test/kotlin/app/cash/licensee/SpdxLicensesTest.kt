/*
 * Copyright (C) 2021 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.licensee

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpdxLicensesTest {
  @Test fun shortestIdentifierAlwaysPreferredRegardlessOfOrder() {
    val json = """
      |{"licenses":[
      |  {
      |    "licenseId": "FOO-1.0",
      |    "name": "Foo License",
      |    "detailsUrl": "./FOO-1.0.html",
      |    "seeAlso": [
      |      "http://example.com/foo"
      |    ]
      |  },
      |  {
      |    "licenseId": "FOO-1.0-some-variant",
      |    "name": "Foo Variant License",
      |    "detailsUrl": "./FOO-1.0-some-variant.html",
      |    "seeAlso": [
      |      "http://example.com/foo"
      |    ]
      |  },
      |  {
      |    "licenseId": "BAR-1.0-some-variant",
      |    "name": "Bar Variant License",
      |    "detailsUrl": "./BAR-1.0-some-variant.html",
      |    "seeAlso": [
      |      "http://example.com/bar"
      |    ]
      |  },
      |  {
      |    "licenseId": "BAR-1.0",
      |    "name": "Bar License",
      |    "detailsUrl": "./BAR-1.0.html",
      |    "seeAlso": [
      |      "http://example.com/bar"
      |    ]
      |  }
      |]}
      |""".trimMargin()
    val spdxLicenses = SpdxLicenses.parseJson(json)
    assertEquals(
      SpdxLicense("FOO-1.0", "Foo License", "https://example.com/foo"),
      spdxLicenses.findByUrl("http://example.com/foo")
    )
    assertEquals(
      SpdxLicense("BAR-1.0", "Bar License", "https://example.com/bar"),
      spdxLicenses.findByUrl("http://example.com/bar")
    )
  }

  @Test fun httpUrlGetsHttpsVariant() {
    val json = """
      |{"licenses":[
      |  {
      |    "licenseId": "FOO-1.0",
      |    "name": "Foo License",
      |    "detailsUrl": "./FOO-1.0.html",
      |    "seeAlso": [
      |      "http://example.com/foo"
      |    ]
      |  },
      |  {
      |    "licenseId": "BAR-1.0",
      |    "name": "Bar License",
      |    "detailsUrl": "./BAR-1.0.html",
      |    "seeAlso": [
      |      "https://example.com/bar"
      |    ]
      |  }
      |]}
      |""".trimMargin()
    val spdxLicenses = SpdxLicenses.parseJson(json)
    assertEquals(
      SpdxLicense("FOO-1.0", "Foo License", "https://example.com/foo"),
      spdxLicenses.findByUrl("http://example.com/foo")
    )
    assertEquals(
      SpdxLicense("FOO-1.0", "Foo License", "https://example.com/foo"),
      spdxLicenses.findByUrl("https://example.com/foo")
    )
    assertNull(spdxLicenses.findByUrl("http://example.com/bar"))
    assertEquals(
      SpdxLicense("BAR-1.0", "Bar License", "https://example.com/bar"),
      spdxLicenses.findByUrl("https://example.com/bar")
    )
  }

  @Test fun spdxUrlSupported() {
    val json = """
      |{"licenses":[
      |  {
      |    "licenseId": "FOO-1.0",
      |    "name": "Foo License",
      |    "detailsUrl": "./FOO-1.0.html",
      |    "seeAlso": [
      |      "http://example.com/foo"
      |    ]
      |  }
      |]}
      |""".trimMargin()
    val spdxLicenses = SpdxLicenses.parseJson(json)
    assertEquals(
      SpdxLicense("FOO-1.0", "Foo License", "https://example.com/foo"),
      spdxLicenses.findByUrl("http://example.com/foo")
    )
    assertEquals(
      SpdxLicense("FOO-1.0", "Foo License", "https://example.com/foo"),
      spdxLicenses.findByUrl("https://spdx.org/licenses/FOO-1.0.html")
    )
  }
}
