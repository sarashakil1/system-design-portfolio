package com.portfolio.shortener;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Base62Test {
    @Test
    void encodesKnownValues() {
        assertThat(Base62.encode(0)).isEqualTo("0");
        assertThat(Base62.encode(61)).isEqualTo("Z");
        assertThat(Base62.encode(62)).isEqualTo("10");
        assertThat(Base62.encode(125)).isEqualTo("21");
    }

    @Test
    void decodesKnownValues() {
        assertThat(Base62.decode("21")).isEqualTo(125);
        assertThat(Base62.decode("18")).isEqualTo(70);
        assertThat(Base62.decode("2Bi")).isEqualTo(10000);

    }

    @Test
    void roundTripsRandomIds() {
        Random random = new Random(42);   // fixed seed: same numbers every run
        for (int i = 0; i < 1_000; i++) {
            long id = random.nextLong(0, Long.MAX_VALUE);
            assertThat(Base62.decode(Base62.encode(id))).isEqualTo(id);
        }
    }

    @Test
    void rejectsNegativeId() {
        assertThatThrownBy(() -> Base62.encode(-1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsEmptyCode() {
        assertThatThrownBy(() -> Base62.decode(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidChar() {
        assertThatThrownBy(() -> Base62.decode("ab-"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsTooLargeCode() {
        assertThatThrownBy(() -> Base62.decode("zzzzzzzzzzzz"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsTripsLongMaxValue() {
        assertThat(Base62.decode(Base62.encode(Long.MAX_VALUE))).isEqualTo(Long.MAX_VALUE);
    }
}
