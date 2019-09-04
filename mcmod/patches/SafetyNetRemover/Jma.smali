.class public final Lcom/Jma;
.super Ljava/lang/Object;
.source "SourceFile"

# interfaces
.implements Lcom/xV;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;",
        "Lcom/xV<",
        "TT;>;"
    }
.end annotation


# instance fields
.field public final synthetic a:Lcom/Gma;

.field public final synthetic b:[B


# direct methods
.method public constructor <init>(Lcom/Gma;[B)V
    .locals 0

    iput-object p1, p0, Lcom/Jma;->a:Lcom/Gma;

    iput-object p2, p0, Lcom/Jma;->b:[B

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public final a(Lcom/vV;)V
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/vV<",
            "Ljava/lang/String;",
            ">;)V"
        }
    .end annotation

    if-eqz p1, :cond_0

    .line 1
    check-cast p1, Lcom/_W$a;
    const-string v1, "REPLACE"
    invoke-virtual {p1, v1}, Lcom/_W$a;->a(Ljava/lang/Object;)V

    return-void

    :cond_0
    const-string p1, "emitter"

    .line 8
    invoke-static {p1}, Lcom/XY;->a(Ljava/lang/String;)V

    const/4 p1, 0x0

    throw p1
.end method
